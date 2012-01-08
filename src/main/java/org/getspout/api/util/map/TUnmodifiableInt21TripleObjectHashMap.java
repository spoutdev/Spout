/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.util.map;

import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;

/**
 * A simplistic unmodifiable map that supports a 3 21 bit integers for keys, using a trove long Object hashmap in the backend. 1 bit is wasted.
 */
public class TUnmodifiableInt21TripleObjectHashMap<K> extends TInt21TripleObjectHashMap<K>{
	public TUnmodifiableInt21TripleObjectHashMap(TInt21TripleObjectHashMap<K> map) {
		this.map = new TUnmodifiableLongObjectMap<K>(map.getInternalMap());
	}
	
	/**
	 * Replaces the internal immutable map with this new one
	 * @param newMap to replace
	 */
	public void update(TInt21TripleObjectHashMap<K> newMap) {
		this.map = new TUnmodifiableLongObjectMap<K>(newMap.getInternalMap());
	}
}
