/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.util.config.ini;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.AbstractConfiguration;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.FileConfiguration;
import org.spout.api.util.config.commented.CommentedConfiguration;
import org.spout.api.util.config.commented.CommentedConfigurationNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.spout.api.util.config.commented.CommentedConfigurationNode.LINE_SEPARATOR;

/**
 * This class handles reading and writing configuration nodes in the INI format.
 * Because the INI format is fairly loose, this class can read INI files written with a few formats.
 * However, when writing INI files the configuration's settings will override what was previously in the file
 *
 * The INI format also has a few limitations over other formats.
 * <ul>
 *     <li>Limited hierarchy: The format can only have one level of children (sections)</li>
 *     <li>Extremely basic datatype support: The configuration can split up lists, but otherwise all
 *     values are stored as strings. Maps are not supported.</li>
 *     <li>Saving files exactly as they were loaded is in some cases impossible. </li>
 * </ul>
 *
 * An example INI file is as follows:
 * <pre>
 *     # All nodes outside of a section are given above the first section specifier
 *     # The : character is a valid key-value splitter, but this implementation currently only writes with the '=' splitter
 *     sectionless-node: see here!
 *
 *     # This is a section
 *     # These sections are the only way of splitting up configuration settings
 *     [section]
 *     key=value
 *     anotherkey = list, of, values
 *
 *
 *     [anothersection]
 *     # Because this value is surrounded by quotes, this is not a list
 *     # Both sections and keys can have comments. These comments are read and written from the configuration file
 *     keys = "not, a, list"
 *
 * </pre>
 */
public class IniConfiguration extends AbstractConfiguration implements CommentedConfiguration, FileConfiguration {
	public static final char COMMENT_CHAR_SEMICOLON = ';';
	public static final char COMMENT_CHAR_HASH = '#';
	public static final Pattern COMMENT_REGEX = Pattern.compile("[" +COMMENT_CHAR_SEMICOLON + COMMENT_CHAR_HASH + "] ?(.*)");
	public static final Pattern SECTION_REGEX = Pattern.compile("\\[(.*)\\]");
	private char preferredCommentChar = COMMENT_CHAR_HASH;
	private final File file;

	public IniConfiguration(File file) {
		this.file = file;
	}

	@Override
	protected Map<String, ConfigurationNode> loadToNodes() throws ConfigurationException {
		Reader stream = null;
		Map<String, ConfigurationNode> nodes = new LinkedHashMap<String, ConfigurationNode>();
		try {
			stream = getReader();
			BufferedReader reader = new BufferedReader(stream);
			String line;
			List<String> comments = new ArrayList<String>();
			List<String> curSection = new ArrayList<String>();
			CommentedConfigurationNode node = null;
			Matcher match;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				match = COMMENT_REGEX.matcher(line);
				if (match.matches()) {
					comments.add(match.group(1));
					continue;
				}
				match = SECTION_REGEX.matcher(line);
				if (match.matches()) {
					if (node != null) {
						for (ConfigurationNode subNode : readNodeSection(node.getPathElements(), curSection.toArray(new String[curSection.size()]))) {
							node.addChild(subNode);
						}
					}
					node = createConfigurationNode(new String[] {match.group(1)}, null);
					if (comments.size() > 0) {
						node.setComment(comments.toArray(new String[comments.size()]));
						comments.clear();
					}
					nodes.put(match.group(1), node);
				} else {
					if (comments.size() > 0) {
						for (String comment : comments) {
							curSection.add(getPreferredCommentChar() + " " + comment);
						}
						comments.clear();
					}
					curSection.add(line);
				}
			}

			if (node != null && curSection.size() > 0) {
				for (ConfigurationNode subNode : readNodeSection(node.getPathElements(), curSection.toArray(new String[curSection.size()]))) {
					node.addChild(subNode);
				}
			}
		} catch (IOException e) {
			throw new ConfigurationException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ignore) {
				}
			}
		}
		return nodes;
	}

	@Override
	protected void saveFromNodes(Map<String, ConfigurationNode> nodes) throws ConfigurationException {
		Writer rawWriter = null;
		BufferedWriter writer = null;
		try {
			rawWriter = getWriter();
			writer = new BufferedWriter(rawWriter);

			List<ConfigurationNode> childlessNodes = new ArrayList<ConfigurationNode>(), sectionNodes = new ArrayList<ConfigurationNode>();
			for (ConfigurationNode node : nodes.values()) {
				if (node.hasChildren()) {
					sectionNodes.add(node);
				} else {
					childlessNodes.add(node);
				}
			}

			if (childlessNodes.size() > 0) {
				writeNodeSection(writer, childlessNodes);
			}

			for (Iterator<ConfigurationNode> i = sectionNodes.iterator(); i.hasNext();) {
				ConfigurationNode node = i.next();
				String[] comment = getComment(node);
				if (comment != null) {
					for (String line : comment) {
						writer.append(getPreferredCommentChar()).append(" ").append(line).append(LINE_SEPARATOR);
					}
				}
				writer.append('[').append(node.getPathElements()[0]).append(']').append(LINE_SEPARATOR);
				writeNodeSection(writer, node.getChildren().values());
				if (i.hasNext()) {
					writer.append(LINE_SEPARATOR);
				}
			}
		} catch (IOException e) {
			throw new ConfigurationException(e);
		} finally {
			if (writer != null) {
				try {
					writer.flush();
				} catch (IOException ignore) {
				}
			}
			if (rawWriter != null) {
				try {
					rawWriter.flush();
					rawWriter.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	/**
	 * This method reads one section of INI configuration data.
	 *
	 * @param parentPath The path of the section containing this data
	 * @param lines The lines of data to read
	 * @return The configuration nodes read from the section
	 * @throws ConfigurationException when an invalid node is specified
	 */
	protected List<ConfigurationNode> readNodeSection(String[] parentPath, String[] lines) throws ConfigurationException {
		List<ConfigurationNode> nodes = new ArrayList<ConfigurationNode>();
		List<String> comment = new ArrayList<String>();
		Matcher match;
		for (String line : lines) {
			match = COMMENT_REGEX.matcher(line);
			if (match.matches()) {
				comment.add(match.group(1));
				continue;
			}
			String[] split = line.split("[=:]", 2);
			if (split.length < 2) {
				throw new ConfigurationException("Key with no value: " + line);
			}
			CommentedConfigurationNode node = createConfigurationNode(ArrayUtils.add(parentPath, split[0].trim()), null);
			node.setValue(fromStringValue(split[1].trim()));
			if (comment.size() > 0) {
				node.setComment(comment.toArray(new String[comment.size()]));
			}
			nodes.add(node);
		}
		return nodes;
	}

	/**
	 * Writes a single section of nodes to the specified Writer
	 * The nodes passed to this method must not have children
	 *
	 * @param writer The Writer to write data to
	 * @param nodes The nodes to write
	 * @throws ConfigurationException when a node cannot be correctly written
	 */
	protected void writeNodeSection(Writer writer, Collection<ConfigurationNode> nodes) throws ConfigurationException {
		try {
			for (ConfigurationNode node : nodes) {
				if (node.hasChildren()) {
					throw new ConfigurationException("Nodes passed to getChildlessNodes must not have children!");
				}
				String[] comment = getComment(node);
				if (comment != null) {
					for (String line : comment) {
						writer.append(getPreferredCommentChar()).append(" ").append(line).append(LINE_SEPARATOR);
					}
				}
				writer.append(node.getPathElements()[node.getPathElements().length - 1]).append("=").append(toStringValue(node.getValue())).append(LINE_SEPARATOR);
			}
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Returns the comment for a given configuration node, with a safe check to make sure
	 * the node is a CommentedConfigurationNode
	 *
	 * @param node The node to get a comment from
	 * @return The node's comment, or null if no comment is present
	 */
	protected static String[] getComment(ConfigurationNode node) {
		String[] comment = null;
		if (node instanceof CommentedConfigurationNode) {
			comment = ((CommentedConfigurationNode) node).getComment();
		}
		return comment;
	}

	/**
	 * Converts a raw String into the correct Object representation for the Configuration node
	 *
	 * @param value The string value
	 * @return The value converted into the correct Object representation
	 */
	public Object fromStringValue(String value) {
		if (value.matches("^([\"']).*\\1$")) { // Quote value
			return value.substring(1, value.length() - 1);
		}

		String[] objects = value.split(", ?");
		if (objects.length == 1) {
			return objects[0];
		}
		return new ArrayList<String>(Arrays.asList(objects));
	}

	/**
	 * Returns the String representation of a configuration value for writing to the file
	 *
	 * @param value
	 * @return
	 */
	public String toStringValue(Object value) {
		if (value == null) {
			return "null";
		}
		if (value.getClass().isArray()) {
			List<Object> toList = new ArrayList<Object>();
			final int length = Array.getLength(value);
			for (int i = 0; i < length; ++i) {
				toList.add(Array.get(value, i));
			}
			value = toList;
		}
		if (value instanceof Collection) {
			StringBuilder builder = new StringBuilder();
			for (Object obj : (Collection<?>) value) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(obj.toString());
			}
			return builder.toString();
		} else {
			String strValue = value.toString();
			if (strValue.contains(",")) {
				strValue = '"' + strValue + '"';
			}
			return strValue;
		}
	}

	@Override
	public CommentedConfigurationNode getNode(String path) {
		return (CommentedConfigurationNode) super.getNode(path);
	}

	@Override
	public CommentedConfigurationNode getNode(String... path) {
		return (CommentedConfigurationNode) super.getNode(path);
	}

	@Override
	public String[] splitNodePath(String path) {
		return getPathSeparatorPattern().split(path, 2);
	}

	@Override
	public String[] ensureCorrectPath(String[] rawPath) {
		if (rawPath.length <= 2) {
			return rawPath;
		}

		return new String[] {
				rawPath[0],
				StringUtils.join(ArrayUtils.subarray(rawPath, 1, rawPath.length), getPathSeparator())
		};
	}

	@Override
	public CommentedConfigurationNode createConfigurationNode(String[] path, Object value) {
		return new CommentedConfigurationNode(getConfiguration(), path, value);
	}

	protected Reader getReader() throws IOException {
		return new InputStreamReader(new FileInputStream(file), "UTF-8");
	}

	protected Writer getWriter() throws IOException {
		return new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	}

	@Override
	public File getFile() {
		return file;
	}

	public char getPreferredCommentChar() {
		return preferredCommentChar;
	}

	public void setPreferredCommentChar(char commentChar) {
		if (commentChar != COMMENT_CHAR_HASH && commentChar != COMMENT_CHAR_SEMICOLON) {
			throw new IllegalArgumentException("Invalid comment char: " + commentChar + "!");
		}
		this.preferredCommentChar = commentChar;
	}
}
