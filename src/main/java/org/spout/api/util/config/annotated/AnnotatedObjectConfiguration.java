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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.ReflectionUtils;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.ConfigurationNodeSource;

public class AnnotatedObjectConfiguration extends AnnotatedConfiguration {
	private final Map<Object, Set<Member>> objectMembers = new HashMap<Object, Set<Member>>();
	private final Map<Object, String[]> objectPaths = new HashMap<Object, String[]>();

	public AnnotatedObjectConfiguration() {
	}

	public AnnotatedObjectConfiguration(Configuration config) {
		super(config);
	}

	public void addObject(Object object, String... path) {
		if (!objectMembers.containsKey(object)) {
			final Set<Member> members = new HashSet<Member>();
			members.addAll(ReflectionUtils.getDeclaredFieldsRecur(object.getClass(), Setting.class));
			members.addAll(ReflectionUtils.getDeclaredMethodsRecur(object.getClass(), Load.class, Save.class));
			objectMembers.put(object, members);
		}
		if (!objectPaths.containsKey(object)) {
			objectPaths.put(object, path);
		}
	}

	public void removeObject(Object object) {
		objectMembers.remove(object);
		objectPaths.remove(object);
	}

	@Override
	public void load(ConfigurationNodeSource source) throws ConfigurationException {
		for (Entry<Object, Set<Member>> entry : objectMembers.entrySet()) {
			final Object object = entry.getKey();
			final String[] objectPath = objectPaths.get(object);
			final Set<Method> methods = new HashSet<Method>();
			for (Member member : entry.getValue()) {
				if (member instanceof Method) {
					final Method method = (Method) member;
					if (method.isAnnotationPresent(Load.class)) {
						methods.add(method);
					}
					continue;
				}
				final Field field = (Field) member;
				field.setAccessible(true);
				String[] fieldPath = field.getAnnotation(Setting.class).value();
				if (fieldPath.length == 0) {
					fieldPath = new String[]{field.getName()};
				}
				final ConfigurationNode fieldNode = source.getNode(ArrayUtils.addAll(objectPath, fieldPath));
				final Object value = fieldNode.getTypedValue(field.getGenericType());
				try {
					if (value != null) {
						field.set(object, value);
					} else {
						fieldNode.setValue(field.getGenericType(), field.get(object));
					}
				} catch (IllegalAccessException ex) {
					throw new ConfigurationException(ex);
				}
			}
			invokeMethods(methods, object, source.getNode(objectPath));
		}
	}

	@Override
	public void save(ConfigurationNodeSource source) throws ConfigurationException {
		for (Entry<Object, Set<Member>> entry : objectMembers.entrySet()) {
			final Object object = entry.getKey();
			final String[] objectPath = objectPaths.get(object);
			final Set<Method> methods = new HashSet<Method>();
			for (Member member : entry.getValue()) {
				if (member instanceof Method) {
					final Method method = (Method) member;
					if (method.isAnnotationPresent(Save.class)) {
						methods.add(method);
					}
					continue;
				}
				final Field field = (Field) member;
				field.setAccessible(true);
				String[] fieldPath = field.getAnnotation(Setting.class).value();
				if (fieldPath.length == 0) {
					fieldPath = new String[]{field.getName()};
				}
				final ConfigurationNode fieldNode = source.getNode(ArrayUtils.addAll(objectPath, fieldPath));
				try {
					fieldNode.setValue(field.getGenericType(), field.get(object));
				} catch (IllegalAccessException ex) {
					throw new ConfigurationException(ex);
				}
			}
			invokeMethods(methods, object, source.getNode(objectPath));
		}
	}

	private void invokeMethods(Set<Method> methods, Object target, ConfigurationNode nodeParam)
			throws ConfigurationException {
		for (Method method : methods) {
			method.setAccessible(true);
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length == 0
					|| !ConfigurationNode.class.isAssignableFrom(parameters[0])) {
				continue;
			}
			try {
				method.invoke(target, nodeParam);
			} catch (IllegalAccessException ex) {
				throw new ConfigurationException(ex);
			} catch (InvocationTargetException ex) {
				throw new ConfigurationException(ex);
			}
		}
	}
}
