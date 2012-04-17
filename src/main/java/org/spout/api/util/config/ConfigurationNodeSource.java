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
	/**
	 * Gets a child of the current node.
	 * Remember that this returns a DIRECT child, without splitting at the {@link org.spout.api.util.config.Configuration#getPathSeparator()}
	 *
	 * @param name The name of the child
	 * @see #getChild(String, boolean)
	 * @return The ConfigurationNode with the child's path.
	 */
	public ConfigurationNode getChild(String name);

	/**
	 * Gets a child of the current node.
	 * Remember that this returns a DIRECT child, without splitting at the {@link org.spout.api.util.config.Configuration#getPathSeparator()}
	 *
	 * @param name The name of the child
	 * @param add Whether to store the configuration to the node structure.
	 * @return The ConfigurationNode with the child's path.
	 */
	public ConfigurationNode getChild(String name, boolean add);

	/**
	 * Adds the provided node as a child to this one
	 * As with the other methods handling children, this method only works with DIRECT children
	 *
	 * @param node The node to add as a child
	 * @return The previous node at the specified path (can be null)
	 */
	public ConfigurationNode addChild(ConfigurationNode node);

	/**
	 * Add the given children. The process for adding children is the same as that for {@link #addChild(ConfigurationNode)}
	 *
	 * @see #addChild(ConfigurationNode)
	 * @param nodes The nodes to add
	 */
	public void addChildren(ConfigurationNode... nodes);

	/**
	 * Remove the child at the specified path, if any, from the configuration structure
	 *
	 * @param key The name of the child
	 * @return The child at the key given, if any
	 */
	public ConfigurationNode removeChild(String key);

	/**
	 * Remove the specified child from the configuration structure
	 *
	 * @param node The node to remove
	 * @return null if unsuccessful, otherwise the node passed in
	 */
	public ConfigurationNode removeChild(ConfigurationNode node);

	/**
	 * Return the children of this node. The returned map is unmodifiable
	 *
	 * @return This node's children
	 */
	public Map<String, ConfigurationNode> getChildren();

	/**
	 * Return the raw Object values of this node's children
	 * ConfigurationNodes are converted into Map<String, Object>s for the result of this method
	 *
	 * @return The node's children values
	 */
	public Map<String, Object> getValues();

	/**
	 * Return the keys in this configuration. If {@code deep} is true, this will also
	 * include paths from child nodes, joined by {@link org.spout.api.util.config.Configuration#getPathSeparator()}
	 * Keys returned are relative to the current node.
	 *
	 * @param deep Whether to also fetch keys in children nodes of this configuration
	 * @return the keys in this configuration
	 */
	public Set<String> getKeys(boolean deep);

	/**
	 * Returns the node at the specified child path, splitting by the configuration's
	 * path separator. This can return both direct children and indirect children.
	 * @see #getNode(String...) for more information on how this method behaves. (This is
	 * also the method called when a path given contains the path separator)
	 *
	 * @param path The path to get a node at
	 * @return The node at the specified path
	 */
	public ConfigurationNode getNode(String path);

	/**
	 * Get a child node of this node source, going across multiple levels.
	 * If there is no node at the specified path a detached configuration node will be
	 * returned, which will be attached to {@link #getConfiguration()} if its value is set or a child is added.
	 *
	 * @param path The path elements to get to the requested node.
	 * @return The child node. Never null.
	 */
	public ConfigurationNode getNode(String... path);

	/**
	 * Returns whether this node source has children.
	 * This is the same as running {@code getChildren().size() > 0}
	 *
	 * @return whether this node source has children
	 */
	public boolean hasChildren();

	/**
	 * Returns the configuration this node source is attached to.
	 * This may return the same object if this {@link ConfigurationNodeSource} is a Configuration.
	 *
	 * @return the attached configuration.
	 */
	public Configuration getConfiguration();

	/**
	 * Returns the elements of the configuration path going to this node source. Can return
	 * an empty array if we are at the root of the tree.
	 *
	 * @return the elements to here
	 */
	public String[] getPathElements();
}
