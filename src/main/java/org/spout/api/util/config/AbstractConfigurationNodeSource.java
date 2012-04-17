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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A basic implementation of ConfigurationNodeSource.
 *
 * @author zml2008
 */
public abstract class AbstractConfigurationNodeSource implements ConfigurationNodeSource {
	protected final Map<String, ConfigurationNode> children = new LinkedHashMap<String, ConfigurationNode>();
	protected Configuration config;

	public AbstractConfigurationNodeSource(Configuration config) {
		this.config = config;
	}


	public Configuration getConfiguration() {
		return config;
	}

	@Override
	public ConfigurationNode getChild(String name) {
		return getChild(name, false);
	}

	public ConfigurationNode getChild(String name, boolean add) {
		ConfigurationNode node = children.get(name);
		if (node == null) {
			node = createConfigurationNode(ArrayUtils.add(getPathElements(), name), null);
			if (add) {
				addChild(node);
			}
		}
		return node;
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		ConfigurationNode ret = children.put(node.getPathElements()[node.getPathElements().length - 1], node);
		node.setAttached(true);
		node.setParent(this);
		return ret;
	}

	@Override
	public void addChildren(ConfigurationNode... nodes) {
		for (ConfigurationNode child : nodes) {
			addChild(child);
		}
	}

	@Override
	public ConfigurationNode removeChild(String key) {
		return removeChild(children.get(key));
	}

	/**
	 * Detach a node from this node source and remove its children
	 * If the node's parent isn't this, the method will silently return
	 * After this method, the node will have a parent of null, no children, and be marked as detached
	 *
	 * @param node The node to detach
	 */
	protected void detachChild(ConfigurationNode node) {
		if (node.getParent() != this) {
			return;
		}
		node.setAttached(false);
		node.setParent(null);
		for (Iterator<ConfigurationNode> i = node.children.values().iterator(); i.hasNext();) {
			node.detachChild(i.next());
			i.remove();
		}
	}

	@Override
	public ConfigurationNode removeChild(ConfigurationNode node) {
		if (node != null) {
			if (node.getParent() != this) {
				return null;
			}
			if (children.remove(node.getPathElements()[node.getPathElements().length - 1]) == null) {
				return null;
			}
			detachChild(node);
		}
		return node;
	}

	@Override
	public Map<String, ConfigurationNode> getChildren() {
		return Collections.unmodifiableMap(children);
	}

	@Override
	public Map<String, Object> getValues() {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Map.Entry<String, ConfigurationNode> entry : getChildren().entrySet()) {
			ret.put(entry.getKey(), entry.getValue().getValue());
		}
		return ret;
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		Set<String> keys = new LinkedHashSet<String>(deep ? children.size() * 2 : children.size());
		for (Map.Entry<String, ConfigurationNode> entry : children.entrySet()) {
			keys.add(entry.getKey());
			if (deep) {
				for (String key : entry.getValue().getKeys(true)) {
					keys.add(entry.getKey() + getConfiguration().getPathSeparator() + key);
				}
			}
		}
		return keys;
	}

	@Override
	public ConfigurationNode getNode(String path) {
		if (path.contains(getConfiguration().getPathSeparator())) {
			return getNode(getConfiguration().splitNodePath(path));
		} else {
			return getChild(path);
		}
	}

	@Override
	public ConfigurationNode getNode(String... path) {
		if (path.length == 0) {
			throw new IllegalArgumentException("Path must not be empty!");
		}
		path = getConfiguration().ensureCorrectPath(path);
		ConfigurationNode node = getChild(path[0]);
		for (int i = 1; i < path.length && node != null && node.isAttached(); ++i) {
			node = node.getChild(path[i]);
		}

		return node == null || !node.isAttached() ? createConfigurationNode(path, null) : node;
	}

	public ConfigurationNode createConfigurationNode(String[] path, Object value) {
		return new ConfigurationNode(getConfiguration(), path, value);
	}

	@Override
	public boolean hasChildren() {
		return children.size() > 0;
	}
}
