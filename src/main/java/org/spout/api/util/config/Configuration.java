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

import java.util.regex.Pattern;

/**
 * @author zml2008
 */
public interface Configuration extends ConfigurationNodeSource {
	/**
	 * Load the configuration's values
	 *
	 * @throws ConfigurationException if an error occurs while loading the configuration
	 */
	void load() throws ConfigurationException;

	/**
	 * Save the configuration's values
	 *
	 * @throws org.spout.api.exception.ConfigurationException when an error occurs
	 */
	void save() throws ConfigurationException;

	/**
	 * Adds the given node to the configuration structure
	 * This will attempt to use the node's existing parents in the configuration structure where possible
	 *
	 * @param node The node to add
	 */
	void setNode(ConfigurationNode node);

	/**
	 * The path separator to use with {@link #getNode(String)}
	 * The path separator splits paths as a literal string, not a regular expression.
	 *
	 * @return The configuration's path separator
	 */
	String getPathSeparator();

	/**
	 * Sets this configuration's path separator. More information on how the path separator
	 * functions in {@link #getPathSeparator()}
	 *
	 * @see #getPathSeparator()
	 * @param pathSeparator The path separator
	 */
	void setPathSeparator(String pathSeparator);

	Pattern getPathSeparatorPattern();

	/**
	 * Whether this configuration writes default values (from {@link ConfigurationNode#getValue(Object)}
	 * to the configuration structure
	 * @return Whether this configuration writes defaults
	 */
	boolean doesWriteDefaults();

	/**
	 * Sets whether this configuration writes defaults
	 *
	 * @see #doesWriteDefaults() for info on what this means
	 * @param writesDefaults Whether this configuration writes defaults
	 */
	void setWritesDefaults(boolean writesDefaults);

	/**
	 * Split the provided path into a string array suitable for accessing the correct configuration children.
	 * Normally this just splits the path with the {@link #getPathSeparator()}, but can limit
	 * how deep a child path can go or whether this configuration can even have children.
	 *
	 * @param path The path to split
	 * @return The connectly split path.
	 */
	String[] splitNodePath(String path);

	/**
	 * Make sure the provided path meets the requirements. A correct implementation of
	 * Configuration will impose the same restrictions on this and {@link #splitNodePath(String)},
	 * so invoking this method on an array from {@link #splitNodePath(String)} would return the original array.
	 *
	 * @param rawPath The raw path of the configuration
	 * @return The corrected input path
	 */
	String[] ensureCorrectPath(String[] rawPath);
}
