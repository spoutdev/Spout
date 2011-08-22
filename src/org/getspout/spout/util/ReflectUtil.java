/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.util;

import java.lang.reflect.Field;

public class ReflectUtil {
	
	public static void transferField(Object src, Object dest, String fieldName) {
		try {
			Field field = getField(src, fieldName);
			field.setAccessible(true);
			Object temp = field.get(src);
			field = getField(dest, fieldName);
			field.setAccessible(true);
			field.set(dest, temp);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field getField(Object o, String fieldName) throws NoSuchFieldException {
		return getField(o.getClass(), fieldName);
	}

	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			return field;
		} catch (NoSuchFieldException e) {
			Class<?> superclass = clazz.getSuperclass();
			if(superclass == null) {
				throw e;
			} else {
				return getField(superclass, fieldName);
			}
		}
	}

}
