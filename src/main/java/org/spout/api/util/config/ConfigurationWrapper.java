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

	@Override
	public Configuration getConfiguration() {
		if (config == null) {
			throw new IllegalArgumentException("The Configuration for a " + getClass().getSimpleName() + " is not set!");
		}
		return config;
	}

	public void setConfiguration(Configuration config) {
		this.config = config;
	}

	@Override
	public void load() throws ConfigurationException {
		getConfiguration().load();
	}

	@Override
	public void save() throws ConfigurationException {
		getConfiguration().save();
	}

	@Override
	public void setNode(ConfigurationNode node) {
		getConfiguration().setNode(node);
	}

	@Override
	public String getPathSeparator() {
		return getConfiguration().getPathSeparator();
	}

	@Override
	public void setPathSeparator(String pathSeparator) {
		getConfiguration().setPathSeparator(pathSeparator);
	}

	@Override
	public Pattern getPathSeparatorPattern() {
		return getConfiguration().getPathSeparatorPattern();
	}

	@Override
	public boolean doesWriteDefaults() {
		return getConfiguration().doesWriteDefaults();
	}

	@Override
	public void setWritesDefaults(boolean writesDefaults) {
		getConfiguration().setWritesDefaults(writesDefaults);
	}

	@Override
	public String[] splitNodePath(String path) {
		return getConfiguration().splitNodePath(path);
	}

	@Override
	public String[] ensureCorrectPath(String[] rawPath) {
		return getConfiguration().ensureCorrectPath(rawPath);
	}

	@Override
	public ConfigurationNode getChild(String name) {
		return getConfiguration().getChild(name);
	}

	@Override
	public ConfigurationNode getChild(String name, boolean add) {
		return getConfiguration().getChild(name, add);
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		return getConfiguration().addChild(node);
	}

	@Override
	public void addChildren(ConfigurationNode... nodes) {
		getConfiguration().addChildren(nodes);
	}

	@Override
	public ConfigurationNode removeChild(String key) {
		return getConfiguration().removeChild(key);
	}

	@Override
	public ConfigurationNode removeChild(ConfigurationNode node) {
		return getConfiguration().removeChild(node);
	}

	@Override
	public Map<String, ConfigurationNode> getChildren() {
		return getConfiguration().getChildren();
	}

	@Override
	public Map<String, Object> getValues() {
		return getConfiguration().getValues();
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return getConfiguration().getKeys(deep);
	}

	@Override
	public ConfigurationNode getNode(String path) {
		return getConfiguration().getNode(path);
	}

	@Override
	public ConfigurationNode getNode(String... path) {
		return getConfiguration().getNode(path);
	}

	@Override
	public boolean hasChildren() {
		return getConfiguration().hasChildren();
	}

	@Override
	public String[] getPathElements() {
		return getConfiguration().getPathElements();
	}
}
