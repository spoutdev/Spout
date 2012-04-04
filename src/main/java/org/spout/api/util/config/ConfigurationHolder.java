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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zml2008
 */
public class ConfigurationHolder extends ConfigurationNode {
	private Configuration configuration;
	private Object def;

	public ConfigurationHolder(Configuration config, Object def, String... path) {
		super(config, path);
		this.def = def;
	}

	public ConfigurationHolder(Object value, String... path) {
		this(null, value, path);
	}

	private ConfigurationNode getNode() {
		return getConfiguration().getNode(getPathEntries());
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration config) {
		this.configuration = config;
	}

	@Override
	public boolean getBoolean(boolean def) {
		return getNode().getBoolean(def);
	}

	@Override
	public int getInt(int def) {
		return getNode().getInt(def);
	}

	@Override
	public long getLong(long def) {
		return getNode().getLong(def);
	}

	@Override
	public double getDouble(double def) {
		return getNode().getDouble(def);
	}

	@Override
	public String getString(String def) {
		return getNode().getString(def);
	}

	@Override
	public Object getValue(Object def) {
		return getNode().getValue(this.def);
	}

	@Override
	public <T> T getTypedValue(Class<T> type, T def) {
		return getNode().getTypedValue(type, def);
	}

	@Override
	public Object setValue(Object value) {
		return getNode().setValue(value);
	}

	@Override
	public List<?> getList(List<?> def) {
		return getNode().getList(def);
	}

	@Override
	public List<String> getStringList(List<String> def) {
		return getNode().getStringList(def);
	}

	@Override
	public List<Integer> getIntegerList(List<Integer> def) {
		return getNode().getIntegerList(def);
	}

	@Override
	public List<Double> getDoubleList(List<Double> def) {
		return getNode().getDoubleList(def);
	}

	@Override
	public List<Boolean> getBooleanList(List<Boolean> def) {
		return getNode().getBooleanList(def);
	}

	@Override
	public ConfigurationNode getChild(String name) {
		return getNode().getChild(name);
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		return getNode().addChild(node);
	}

	@Override
	public ConfigurationNode removeChild(String key) {
		return getNode().removeChild(key);
	}

	@Override
	public ConfigurationNode removeChild(ConfigurationNode node) {
		return getNode().removeChild(node);
	}

	@Override
	public Map<String, ConfigurationNode> getChildren() {
		return getNode().getChildren();
	}

	@Override
	public Map<String, Object> getValues() {
		return getNode().getValues();
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return getNode().getKeys(deep);
	}

	@Override
	public ConfigurationNode getNode(String path) {
		return getNode().getNode(path);
	}

	@Override
	public ConfigurationNode getNode(String... path) {
		return getNode().getNode(path);
	}

	@Override
	public boolean hasChildren() {
		return getNode().hasChildren();
	}

	protected boolean isAttached() {
		return getNode().isAttached();
	}

	protected void setAttached(boolean value) {
		getNode().setAttached(value);
	}

	public ConfigurationNodeSource getParent() {
		return getNode().getParent();
	}

	protected void setParent(ConfigurationNodeSource parent) {
		if (parent == this) {
			throw new IllegalArgumentException("Attempted inheritance involving a ConfigurationHolder!");
		}
		getNode().setParent(parent);
	}
}
