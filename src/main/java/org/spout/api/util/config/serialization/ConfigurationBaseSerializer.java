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
package org.spout.api.util.config.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.MapConfiguration;
import org.spout.api.util.config.annotated.AnnotatedConfiguration;

public class ConfigurationBaseSerializer extends Serializer {
	private static final Map<Class<? extends AnnotatedConfiguration>,
			Constructor<? extends AnnotatedConfiguration>> CACHED_CONSTRUCTORS =
			new HashMap<Class<? extends AnnotatedConfiguration>, Constructor<? extends AnnotatedConfiguration>>();

	public ConfigurationBaseSerializer() {
		setAllowsNullValue(true);
	}

	@Override
	public boolean isApplicable(GenericType type) {
		return AnnotatedConfiguration.class.isAssignableFrom(type.getMainType()) ;
	}

	@Override
	public boolean isApplicableDeserialize(GenericType type, Object value) {
		return super.isApplicableDeserialize(type, value) && (value == null || value instanceof Map);
	}

	@Override
	protected int getParametersRequired() {
		return -1;
	}

	@Override
	protected Object handleDeserialize(GenericType type, Object value) {
		if (value == null) {
			value = new HashMap<Object, Object>();
		}

		Class<? extends AnnotatedConfiguration> configClass = type.getMainType().asSubclass(AnnotatedConfiguration.class);
		Constructor<? extends AnnotatedConfiguration> constructor = CACHED_CONSTRUCTORS.get(configClass);
		if (constructor == null) {
			try {
				constructor = configClass.getDeclaredConstructor(Configuration.class);
				constructor.setAccessible(true);
			} catch (NoSuchMethodException e) {
				return null;
			}
			CACHED_CONSTRUCTORS.put(configClass, constructor);
		}
		AnnotatedConfiguration config = null;
		MapConfiguration rawConfig = new MapConfiguration((Map<?, ?>) value);

		try {
			config = constructor.newInstance(rawConfig);
		} catch (InstantiationException ignore) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
		}

		if (config != null) {
			try {
				config.load();
			} catch (ConfigurationException e) {
				e.printStackTrace();
				return null;
			}
		}

		return config;
	}

	@Override
	protected Object handleSerialize(GenericType type, Object val) {
		MapConfiguration config = new MapConfiguration();
		try {
			((AnnotatedConfiguration) val).save(config);
			config.save();
		} catch (ConfigurationException e) {
			return null;
		}
		return config.getMap();
	}
}
