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

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.spout.api.exception.ConfigurationException;

/**
 * A parent class for implementations of Configuration that wrap other Configurations.
 */
public abstract class ConfigurationWrapper implements Configuration {
	private Configuration config;

	public ConfigurationWrapper() {
		this(null);
	}

	public ConfigurationWrapper(Configuration config) {
		this.config = config;
	}

	public Configuration getConfiguration() {
		if (config == null) {
			throw new IllegalArgumentException("The Configuration for a " + getClass().getSimpleName() + " is not set!");
		}
		return config;
	}

	public void setConfiguration(Configuration config) {
		this.config = config;
	}

	public void load() throws ConfigurationException {
		getConfiguration().load();
	}

	public void save() throws ConfigurationException {
		getConfiguration().save();
	}

	public void setNode(ConfigurationNode node) {
		getConfiguration().setNode(node);
	}

	public String getPathSeparator() {
		return getConfiguration().getPathSeparator();
	}

	public void setPathSeparator(String pathSeparator) {
		getConfiguration().setPathSeparator(pathSeparator);
	}

	public Pattern getPathSeparatorPattern() {
		return getConfiguration().getPathSeparatorPattern();
	}

	public boolean doesWriteDefaults() {
		return getConfiguration().doesWriteDefaults();
	}

	public void setWritesDefaults(boolean writesDefaults) {
		getConfiguration().setWritesDefaults(writesDefaults);
	}

	public String[] splitNodePath(String path) {
		return getConfiguration().splitNodePath(path);
	}

	public String[] ensureCorrectPath(String[] rawPath) {
		return getConfiguration().ensureCorrectPath(rawPath);
	}

	public ConfigurationNode getChild(String name) {
		return getConfiguration().getChild(name);
	}

	public ConfigurationNode getChild(String name, boolean add) {
		return getConfiguration().getChild(name, add);
	}

	public ConfigurationNode addChild(ConfigurationNode node) {
		return getConfiguration().addChild(node);
	}

	public void addChildren(ConfigurationNode... nodes) {
		getConfiguration().addChildren(nodes);
	}

	public ConfigurationNode removeChild(String key) {
		return getConfiguration().removeChild(key);
	}

	public ConfigurationNode removeChild(ConfigurationNode node) {
		return getConfiguration().removeChild(node);
	}

	public Map<String, ConfigurationNode> getChildren() {
		return getConfiguration().getChildren();
	}

	public Map<String, Object> getValues() {
		return getConfiguration().getValues();
	}

	public Set<String> getKeys(boolean deep) {
		return getConfiguration().getKeys(deep);
	}

	public ConfigurationNode getNode(String path) {
		return getConfiguration().getNode(path);
	}

	public ConfigurationNode getNode(String... path) {
		return getConfiguration().getNode(path);
	}

	public boolean hasChildren() {
		return getConfiguration().hasChildren();
	}

	public String[] getPathElements() {
		return getConfiguration().getPathElements();
	}
}
