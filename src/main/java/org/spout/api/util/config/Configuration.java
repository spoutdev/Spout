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
public abstract class Configuration extends AbstractConfigurationNodeSource {
	private String pathSeparator;
	private Pattern pathSeparatorPattern;
	private boolean writesDefaults;

	/**
	 * Implementations can use this method to provide the necessary data for calls of load.
	 * @return A map with raw configuration data
	 * @throws ConfigurationException when an error occurs while loading.
	 */
	protected abstract Map<?, ?> loadToMap() throws ConfigurationException;

	/**
	 * Save the  data from this configuration. This method is called from {@link #save()}
	 * @param map Configuration as a set of nested Maps
	 * @throws ConfigurationException When an error occurs while saving the given data.
	 */
	protected abstract void saveFromMap(Map<?, ?> map) throws ConfigurationException;

	public Configuration() {
		super(null);
		this.config = this;
		setPathSeparator(".");
		setWritesDefaults(true);
	}

	public void load() throws ConfigurationException {
		Map<?, ?> rawValues = loadToMap();
		for (Map.Entry<?, ?> entry : rawValues.entrySet()) {
			addChild(createConfigurationNode(new String[] {entry.getKey().toString()}, entry.getValue(), false));
		}
	}

	/**
	 * Save the configuration's values
	 * @throws ConfigurationException when an error occurs
	 */
	public void save() throws ConfigurationException {
		saveFromMap(getValues());
	}

	public void setNode(ConfigurationNode node) {
		String[] path = node.getPathElements();
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Path must be specified!");
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

	@Override
	public String[] getPathElements() {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}
}
