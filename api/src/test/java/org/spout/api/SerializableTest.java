/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.spout.api.util.ReflectionUtils;

public class SerializableTest {
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		List<Class<?>> classes = ReflectionUtils.getClassesForPackage("org.spout.api", true);
		for (Class<?> clazz : classes) {
			if (Serializable.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
				//Top level has custom serialization, skip
				if (ReflectionUtils.hasCustomSerialization(clazz)) {
					continue;
				}
				Class<?> superclazz = clazz;
				while (superclazz != null) {
					//Could be superclass, can't be sure it's used, need to check all superclasses for serializability
					if (ReflectionUtils.hasCustomSerialization(superclazz)) {
						//System.out.println("Class " + superclazz.getName() + " has custom serialization, skipping field checks");
					} else {
						for (Field f : superclazz.getDeclaredFields()) {
							f.setAccessible(true);
							//Not serialized
							if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
								continue;
							}
							//Automatically serializable
							if (f.isEnumConstant() || f.getType().isPrimitive()) {
								continue;
							}
							//Result of inner classes, ignore
							if (f.isSynthetic()) {
								continue;
							}
							if (!Serializable.class.isAssignableFrom(f.getType())) {
								try {
									fail("Unserializable field " + f.getName() + " found in " + superclazz.getName());
								} catch (Throwable t) {
									collector.addError(t);;
								}
							}
						}
					}

					superclazz = superclazz.getSuperclass();
				}
			}
		}
	}
}
