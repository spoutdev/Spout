/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.util.config.yaml;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.FileConfiguration;
import org.spout.api.util.config.MapBasedConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A configuration that loads from a YAML file
 * @author zml2008
 */
public class YamlConfiguration extends MapBasedConfiguration implements FileConfiguration {
	public static final String LINE_BREAK = DumperOptions.LineBreak.getPlatformLineBreak().getString();
	public static final char COMMENT_CHAR = '#';
	public static final Pattern COMMENT_REGEX = Pattern.compile(COMMENT_CHAR + " ?(.*)");
	private final File file;
	private final Yaml yaml;
	private String header = null;

	public YamlConfiguration(File file) {
		this.file = file;

		DumperOptions options = new DumperOptions();

		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		yaml = new Yaml(new SafeConstructor(), new EmptyNullRepresenter(), options);
	}

	@Override
	protected Map<?, ?> loadToMap() throws ConfigurationException {
		BufferedReader in = null;
		try {
			if (!file.exists()) {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}

			in = new BufferedReader(getReader());
			List<String> header = new ArrayList<String>();
			boolean inHeader = true;
			String str;
			StringBuilder buffer = new StringBuilder(10000);
			while ((str = in.readLine()) != null) {
				if (inHeader) {
					if (str.trim().startsWith("#")) {
						header.add(str);
					} else {
						inHeader = false;
					}
				}
				buffer.append(str.replaceAll("\t", "    "));
				buffer.append(LINE_BREAK);
			}

			if (header.size() > 0) {
				setHeader(header.toArray(new String[header.size()]));
			}

			Object val = yaml.load(new StringReader(buffer.toString()));
			if (val instanceof Map<?, ?>) {
				return (Map<?, ?>) val;
			}
		} catch (YAMLException e) {
			throw new ConfigurationException(e);
		} catch (FileNotFoundException ignore) {
		} catch (IOException e) {
			throw new ConfigurationException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ignore) {
			}
		}
		return Collections.emptyMap();
	}

	@Override
	protected void saveFromMap(Map<?, ?> map) throws ConfigurationException {
		BufferedWriter writer = null;

		File parent = file.getParentFile();

		if (parent != null) {
			parent.mkdirs();
		}

		try {
			writer = new BufferedWriter(getWriter());
			if (getHeader() != null) {
				writer.append(getHeader());
				writer.append(LINE_BREAK);
			}

			yaml.dump(map, writer);
		} catch (YAMLException e) {
			throw new ConfigurationException(e);
		} catch (IOException e) {
			throw new ConfigurationException(e);
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public void setHeader(String... headerLines) {
		if (headerLines.length == 1) {
			headerLines = headerLines[0].split(LINE_BREAK);
		}

		StringBuilder header = new StringBuilder();
		for (String line : headerLines) {
			if (header.length() > 0) {
				header.append(LINE_BREAK);
			}

			line = line.trim();
			Matcher matcher = COMMENT_REGEX.matcher(line);
			if (matcher.find()) {
				header.append(matcher.group(1).trim());
			} else {
				header.append(line);
			}
		}

		this.header = header.toString();
	}

	public String getHeader() {
		return header;
	}

	public File getFile() {
		return file;
	}

	protected Reader getReader() throws IOException {
		return new InputStreamReader(new FileInputStream(file), "UTF-8");
	}

	protected Writer getWriter() throws IOException {
		return new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	}
}
