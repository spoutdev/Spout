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

/**
 * This is a configuration holder class that takes another Configuration and wraps some
 * reflection to get all the fields in the subclass that have values of the type {@link ConfigurationHolder}.
 * These fields will be automatically associated with the attached configuration and have
 * their default values loaded into the configuration as needed on load
 */
public abstract class ConfigurationHolderConfiguration extends ConfigurationWrapper  {
	private final List<ConfigurationHolder> holders = new ArrayList<ConfigurationHolder>();

	public ConfigurationHolderConfiguration(Configuration base) {
		super(base);
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
		super.load();
		for (ConfigurationHolder holder : holders) {
			holder.getValue(); // Initialize the ConfigurationHolder's value
		}
	}
}
