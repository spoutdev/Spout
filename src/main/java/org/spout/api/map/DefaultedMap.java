/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.map;

import java.util.Map;

/**
 * An extension of the default Java.util Map interface, that allows
 * a default value to be returned when keys are not present in the map.
 */
public interface DefaultedMap<K, V> extends Map<K, V> {

	/**
	 * Returns the value to which the specified key is mapped,
	 * or the default value if this map contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @param defaultValue to be returned if the key is not found
	 * @return the value the key is mapped to or the default value
	 */
	public V get(Object key, V defaultValue);

}
