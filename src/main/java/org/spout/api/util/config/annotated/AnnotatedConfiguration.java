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
package org.spout.api.util.config.annotated;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spout.api.util.ReflectionUtils;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.ConfigurationNodeSource;

/**
 * The base class for annotated configurations
 * Annotated configurations are created by subclassing ConfigurationBase and having fields annotated with @Setting
 */
public abstract class AnnotatedConfiguration {
	private static final Logger logger = Logger.getLogger(AnnotatedConfiguration.class.getCanonicalName());

	private boolean isConfigured;

	public boolean isConfigured() {
		return isConfigured;
	}

	public void load(ConfigurationNodeSource source) {
		if (getClass().isAnnotationPresent(SettingBase.class)) {
			source = source.getNode(getClass().getAnnotation(SettingBase.class).value());
		}

		for (Field field : ReflectionUtils.getDeclaredFieldsRecur(getClass())) {
			if (!field.isAnnotationPresent(Setting.class)) continue;
			String[] key = field.getAnnotation(Setting.class).value();
			ConfigurationNode node = source.getNode(key);
			final Object value = node.getTypedValue(field.getGenericType());
			try {
				field.setAccessible(true);
				if (value != null) {
					field.set(this, value);
				} else {
					node.setValue(field.getGenericType(), field.get(this));
				}
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, "Error setting configuration value of field: ", e);
				e.printStackTrace();
			}
		}
		isConfigured = true;
	}

	public void save(ConfigurationNodeSource source) {
		if (getClass().isAnnotationPresent(SettingBase.class)) {
			source = source.getNode(getClass().getAnnotation(SettingBase.class).value());
		}
		for (Field field : ReflectionUtils.getDeclaredFieldsRecur(getClass())) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(Setting.class)) continue;
			String[] key = field.getAnnotation(Setting.class).value();
			try {
				source.getNode(key).setValue(field.getGenericType(), field.get(this));
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, "Error getting configuration value of field: ", e);
				e.printStackTrace();
			}
		}
	}
}
