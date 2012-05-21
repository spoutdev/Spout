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
import org.spout.api.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This is a configuration holder class that takes another Configuration and wraps some
 * reflection to get all the fields in the subclass that have values of the type {@link ConfigurationHolder}.
 * These fields will be automatically associated with the attached configuration and have
 * their default values loaded into the configuration as needed on load
 *
 * @author zml2008
 */
public abstract class ConfigurationHolderConfiguration implements Configuration {
	private final Configuration base;
	private final List<ConfigurationHolder> holders = new ArrayList<ConfigurationHolder>();

	public ConfigurationHolderConfiguration(Configuration base) {
		this.base = base;
		for (Field field : ReflectionUtils.getDeclaredFieldsRecur(getClass())) {
			field.setAccessible(true);

			if (ConfigurationHolder.class.isAssignableFrom(field.getType())) {
				try {
					ConfigurationHolder holder = (ConfigurationHolder) field.get(this);
					holder.setConfiguration(getConfiguration());
					holders.add(holder);
				} catch (IllegalAccessException e) {
				}
			}
		}
	}

	public void load() throws ConfigurationException {
		base.load();
		for (ConfigurationHolder holder : holders) {
			holder.getValue(); // Initialize the ConfigurationHolder's value
		}
	}

	public void save() throws ConfigurationException {
		base.save();
	}

	public void setNode(ConfigurationNode node) {
		base.setNode(node);
	}

	public String getPathSeparator() {
		return base.getPathSeparator();
	}

	public void setPathSeparator(String pathSeparator) {
		base.setPathSeparator(pathSeparator);
	}

	public Pattern getPathSeparatorPattern() {
		return base.getPathSeparatorPattern();
	}

	public boolean doesWriteDefaults() {
		return base.doesWriteDefaults();
	}

	public void setWritesDefaults(boolean writesDefaults) {
		base.setWritesDefaults(writesDefaults);
	}

	public String[] splitNodePath(String path) {
		return base.splitNodePath(path);
	}

	public String[] ensureCorrectPath(String[] rawPath) {
		return base.ensureCorrectPath(rawPath);
	}

	public ConfigurationNode getChild(String name) {
		return base.getChild(name);
	}

	public ConfigurationNode getChild(String name, boolean add) {
		return base.getChild(name, add);
	}

	public ConfigurationNode addChild(ConfigurationNode node) {
		return base.addChild(node);
	}

	public void addChildren(ConfigurationNode... nodes) {
		base.addChildren(nodes);
	}

	public ConfigurationNode removeChild(String key) {
		return base.removeChild(key);
	}

	public ConfigurationNode removeChild(ConfigurationNode node) {
		return base.removeChild(node);
	}

	public Map<String, ConfigurationNode> getChildren() {
		return base.getChildren();
	}

	public Map<String, Object> getValues() {
		return base.getValues();
	}

	public Set<String> getKeys(boolean deep) {
		return base.getKeys(deep);
	}

	public ConfigurationNode getNode(String path) {
		return base.getNode(path);
	}

	public ConfigurationNode getNode(String... path) {
		return base.getNode(path);
	}

	public boolean hasChildren() {
		return base.hasChildren();
	}

	public Configuration getConfiguration() {
		return base.getConfiguration();
	}

	public String[] getPathElements() {
		return base.getPathElements();
	}
}
