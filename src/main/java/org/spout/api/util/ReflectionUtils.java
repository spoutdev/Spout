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
package org.spout.api.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Various utility methods that deal with reflection
 */
public class ReflectionUtils {

	/**
	 * Get all the public fields in a class, as well as those in its superclasses (excluding {@link Object})
	 *
	 * @param clazz The class to get all fields in
	 * @return The public fields in the class
	 */
	public static List<Field> getFieldsRecur(Class<?> clazz) {
		return getFieldsRecur(clazz, false);
	}

	/**
	 * Get all the public fields in a class, as well as those in its superclasses
	 *
	 * @see Class#getFields()
	 * @param clazz The class to get all fields in
	 * @param includeObject Whether to include fields in {@link Object}
	 * @return The public fields in the class
	 */
	public static List<Field> getFieldsRecur(Class<?> clazz, boolean includeObject) {
		List<Field> fields = new ArrayList<Field>();
		while (clazz != null && (includeObject || !Object.class.equals(clazz))) {
			fields.addAll(Arrays.asList(clazz.getFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	/**
	 * Get all the fields in a class, as well as those in its superclasses (excluding {@link Object})
	 *
	 * @param clazz The class to get all fields in
	 * @return The fields in the class
	 */
	public static List<Field> getDeclaredFieldsRecur(Class<?> clazz) {
		return getDeclaredFieldsRecur(clazz, false);
	}

	/**
	 * Get all the fields in a class, as well as those in its superclasses
	 *
	 * @see Class#getDeclaredFields()
	 * @param clazz The class to get all fields in
	 * @param includeObject Whether to include fields in {@link Object}
	 * @return The fields in the class
	 */
	public static List<Field> getDeclaredFieldsRecur(Class<?> clazz, boolean includeObject) {
		List<Field> fields = new ArrayList<Field>();
		while (clazz != null && (includeObject || !Object.class.equals(clazz))) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}
}
