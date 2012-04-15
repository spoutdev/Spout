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

import java.util.Map;
import java.util.Set;

/**
 *
 * @author zml2008
 */
public interface ConfigurationNodeSource {
	public ConfigurationNode getChild(String name);
	public ConfigurationNode getChild(String name, boolean add);
	public ConfigurationNode addChild(ConfigurationNode node);
	public void addChildren(ConfigurationNode... nodes);
	public ConfigurationNode removeChild(String key);
	public ConfigurationNode removeChild(ConfigurationNode node);
	public Map<String, ConfigurationNode> getChildren();
	public Map<String, Object> getValues();
	public Set<String> getKeys(boolean deep);

	/**
	 *
	 * @param path
	 * @return
	 */
	public ConfigurationNode getNode(String path);

	/**
	 * Get a child node of this node source, going across multiple levels.
	 * @param path The path elements to get to the requested node.
	 * @return The child node. Never null.
	 */
	public ConfigurationNode getNode(String... path);

	/**
	 * Returns whether this node source has children.
	 * This is the same as running {@code getChildren.size() > 0}
	 * @return whether this node source has children
	 */
	public boolean hasChildren();

	/**
	 * Returns the configuration this node source is attached to.
	 * This may return the same object if this {@link ConfigurationNodeSource} is a Configuration.
	 * @return the attached configuration.
	 */
	public Configuration getConfiguration();

	/**
	 * Returns the elements of the configuration path going to this node source. Can return
	 * an empty array if we are at the root of the tree.
	 * @return the elements to here
	 */
	public String[] getPathElements();
}
