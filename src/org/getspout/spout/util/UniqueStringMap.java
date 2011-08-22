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
