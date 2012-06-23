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
import java.util.HashSet;
import java.util.Set;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.ReflectionUtils;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.ConfigurationNodeSource;
import org.spout.api.util.config.ConfigurationWrapper;

/**
 * The base class for annotated configurations
 * Annotated configurations are created by subclassing ConfigurationBase and having fields annotated with @Setting
 */
public abstract class AnnotatedConfiguration extends ConfigurationWrapper {
	private Set<Field> fields;
	private boolean isConfigured;

	public AnnotatedConfiguration(Configuration baseConfig) {
		super(baseConfig);
	}

	public boolean isConfigured() {
		return isConfigured;
	}

	private Set<Field> getFields() {
		if (this.fields == null) {
			this.fields = new HashSet<Field>(ReflectionUtils.getDeclaredFieldsRecur(getClass()));
		}
		return this.fields;
	}

	public void load(ConfigurationNodeSource source) throws ConfigurationException {
		for (Field field : getFields()) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(Setting.class)) continue;
			String[] key = field.getAnnotation(Setting.class).value();
			ConfigurationNode node = source.getNode(key);
			final Object value = node.getTypedValue(field.getGenericType());
			try {
				if (value != null) {
					field.set(this, value);
				} else {
					node.setValue(field.getGenericType(), field.get(this));
				}
			} catch (IllegalAccessException e) {
				throw new ConfigurationException(e);
			}
		}
		isConfigured = true;
	}

	public void save(ConfigurationNodeSource source) throws ConfigurationException {
		for (Field field : getFields()) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(Setting.class)) continue;
			String[] key = field.getAnnotation(Setting.class).value();
			try {
				source.getNode(key).setValue(field.getGenericType(), field.get(this));
			} catch (IllegalAccessException e) {
				throw new ConfigurationException(e);
			}
		}
	}

	public void load() throws ConfigurationException {
		super.load();
		load(this);

	}

	public void save() throws ConfigurationException {
		save(this);
		super.save();
	}
}
