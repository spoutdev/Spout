package org.getspout.spout.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueStringMap {

	private static final ConcurrentHashMap<Integer,String> reverse = new ConcurrentHashMap<Integer,String>();
	private static final ConcurrentHashMap<String,Integer> forward = new ConcurrentHashMap<String,Integer>();
	
	private static final AtomicInteger idCounter = new AtomicInteger(0);
	
	/**
	 * Associates a unique id for each string
	 * 
	 * These associations do not persist over reloads or server restarts
	 * 
	 * @param string the string to be associated
	 * @return the id associated with the string.
	 */
	
	public static int getId(String string) {
		
		Integer id;
		
		id = forward.get(string);
		if (id != null) {
			return id;
		}
		
		id = idCounter.incrementAndGet();
		
		Integer oldId = forward.putIfAbsent(string, id);
		
		if (oldId == null) { // Success
			reverse.put(id, string);
			return id;
		} else { // Another thread created the association
			return forward.get(string);
		}
		
	}
	
	/**
	 * Returns the id associated with a string
	 * 
	 * These associations do not persist over reloads or server restarts
	 * 
	 * @param id the id
	 * @return the string associated with the id, or null if no string is associated
	 */
	
	public static String getString(int id) {
		return reverse.get(id);
	}
	
}
