package org.spout.api.generic;

import java.util.HashMap;

/**
 * Defines a generic type-to-id matching store.
 * This can be used for Widgets, Layouts, Screens or Entities. 
 * @param <T> the type
 */
public class GenericType<T> {
	
	private Class<? extends T> clazz;
	private int id;
	
	private static HashMap<Class<?>, HashMap<Integer, GenericType<?>>> types = new HashMap<Class<?>, HashMap<Integer,GenericType<?>>>();
	
	public GenericType(Class<? extends T> clazz, int id) {
		getTypes(clazz).put(id, this);
		this.id = id;
		this.clazz = clazz;
	}
	
	public Class<? extends T> getClazz() {
		return clazz;
	}
	
	public int getId() {
		return id;
	}
	
	public static GenericType<?> getType(Class<?> clazz, int id) {
		return getTypes(clazz).get(id);
	}
	
	private static HashMap<Integer, GenericType<?>> getTypes(Class<?> clazz) {
		if(types.containsKey(clazz)) {
			return types.get(clazz);
		}
		HashMap<Integer, GenericType<?>> ret = new HashMap<Integer, GenericType<?>>();
		types.put(clazz, ret);
		return ret;
	}
}
