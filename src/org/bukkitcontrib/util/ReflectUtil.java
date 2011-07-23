package org.bukkitcontrib.util;

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
