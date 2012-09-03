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
package org.spout.api.util.config;

import org.spout.api.exception.ConfigurationException;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A basic implementation of {@link Configuration} using {@link ConfigurationNodeSource}
 * method implementations from {@link AbstractConfigurationNodeSource}
 */
public abstract class AbstractConfiguration extends AbstractConfigurationNodeSource implements Configuration {
	private String pathSeparator;
	private Pattern pathSeparatorPattern;
	private boolean writesDefaults;

	public AbstractConfiguration() {
		super(null);
		this.config = this;
		setPathSeparator(".");
		setWritesDefaults(true);
	}

	/**
	 * Implementations can use this method to provide the necessary data for calls of load.
	 * @return A map with raw configuration data
	 * @throws ConfigurationException when an error occurs while loading.
	 */
	protected abstract Map<String, ConfigurationNode> loadToNodes() throws ConfigurationException;

	/**
	 * Save the  data from this configuration. This method is called from {@link #save()}
	 * @param nodes Configuration as a set of nested ConfigurationNodes
	 * @throws ConfigurationException When an error occurs while saving the given data.
	 */
	protected abstract void saveFromNodes(Map<String, ConfigurationNode> nodes) throws ConfigurationException;

	@Override
	public void load() throws ConfigurationException {
		// Kill the existing children
		for (ConfigurationNode child : children.values()) {
			detachChild(child);
		}
		children.clear();

		Map<String, ConfigurationNode> rawValues = loadToNodes();
		// Load the new children
		for (Map.Entry<String, ConfigurationNode> entry : rawValues.entrySet()) {
			addChild(entry.getValue());
		}
	}

	@Override
	public void save() throws ConfigurationException {
		saveFromNodes(getChildren());
	}

	@Override
	public void setNode(ConfigurationNode node) {
		String[] path = node.getPathElements();
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Path must be specified!");
		} else if (path.length == 1) {
			addChild(node);
			return;
		}
		// Gather the parents the node already has for later reference
		ConfigurationNode parentCollector = node;
		ConfigurationNode[] parents = new ConfigurationNode[path.length];
		int index = parents.length - 1;
		parents[parents.length - 1] = node;
		while (index > 0 && parentCollector.getParent() != null) {
			ConfigurationNode parentNode = parentCollector.getParent() instanceof ConfigurationNode ? (ConfigurationNode) parentCollector.getParent() : null;
			if (parentNode == null) {
				break;
			}
			parents[--index] = parentNode;
			parentCollector = parentNode;
		}

		// Try to use existing parents where they are present
		ConfigurationNode parent;
		if (parents[0] != null) {
			parent = parents[0];
			if (!parent.isAttached() || parent.getParent() != this) {
				addChild(parents[0]);
			}
		} else {
			parent = getChild(path[0]);
		}

		ConfigurationNodeSource oldParent;
		for (int i = 1; i < path.length - 1; ++i) {
			oldParent = parent;
			parent = parents[i] != null ? parents[i] : oldParent.getChild(path[i], true);

			if (i != path.length - 2 && !parent.isAttached()) {
				oldParent.addChild(parent);
			}
		}
		parent.addChild(node);
	}

	@Override
	public String getPathSeparator() {
		return pathSeparator;
	}

	@Override
	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
		this.pathSeparatorPattern = Pattern.compile(Pattern.quote(pathSeparator));
	}

	@Override
	public Pattern getPathSeparatorPattern() {
		return pathSeparatorPattern;
	}

	@Override
	public boolean doesWriteDefaults() {
		return writesDefaults;
	}

	@Override
	public void setWritesDefaults(boolean writesDefaults) {
		this.writesDefaults = writesDefaults;
	}

	@Override
	public String[] splitNodePath(String path) {
		return getPathSeparatorPattern().split(path);
	}

	@Override
	public String[] ensureCorrectPath(String[] rawPath) {
		return rawPath;
	}

	@Override
	public String[] getPathElements() {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}
}
