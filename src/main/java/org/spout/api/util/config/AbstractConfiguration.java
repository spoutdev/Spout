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
package org.spout.api.util.config;

import org.apache.commons.lang3.ArrayUtils;
import org.spout.api.exception.ConfigurationException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author zml2008
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

	public void load() throws ConfigurationException {
		Map<String, ConfigurationNode> rawValues = loadToNodes();
		for (Map.Entry<String, ConfigurationNode> entry : rawValues.entrySet()) {
			addChild(entry.getValue());
		}
	}

	public void save() throws ConfigurationException {
		saveFromNodes(getChildren());
	}

	public void setNode(ConfigurationNode node) {
		String[] path = node.getPathElements();
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Path must be specified!");
		} else if (path.length == 1) {
			addChild(node);
			return;
		}

		ConfigurationNode parent = getChild(path[0], true);
		ConfigurationNodeSource oldParent;
		for (int i = 1; i < path.length - 1; ++i) {
			oldParent = parent;
			parent = oldParent.getChild(path[i], true);

			if (i != path.length - 2 && !parent.isAttached()) {
				oldParent.addChild(parent);
			}
		}
		parent.addChild(node);
	}

	public String getPathSeparator() {
		return pathSeparator;
	}

	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
		this.pathSeparatorPattern = Pattern.compile(Pattern.quote(pathSeparator));
	}

	public Pattern getPathSeparatorPattern() {
		return pathSeparatorPattern;
	}

	public boolean doesWriteDefaults() {
		return writesDefaults;
	}

	public void setWritesDefaults(boolean writesDefaults) {
		this.writesDefaults = writesDefaults;
	}

	public String[] splitNodePath(String path) {
		return getPathSeparatorPattern().split(path);
	}

	public String[] ensureCorrectPath(String[] rawPath) {
		return rawPath;
	}

	@Override
	public String[] getPathElements() {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}
}
