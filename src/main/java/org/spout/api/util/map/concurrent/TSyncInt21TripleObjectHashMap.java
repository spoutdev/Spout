/*
 * This file is part of SpoutAPI (http://www.spout.org/).
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
package org.spout.api.util.map.concurrent;

import org.spout.api.util.map.TInt21TripleObjectHashMap;

/**
 * A simplistic map that supports a 3 21 bit integers for keys, using a trove long Object hashmap in the backend. 1 bit is wasted.
 * 
 * This map is backed by a read/write lock synchronised map.
 * 
 * @param <K> the value type
 */
public class TSyncInt21TripleObjectHashMap<K> extends TInt21TripleObjectHashMap<K> {

	public TSyncInt21TripleObjectHashMap() {
		map = new TSyncLongObjectHashMap<K>(100);
	}

	public TSyncInt21TripleObjectHashMap(int capacity) {
		map = new TSyncLongObjectHashMap<K>(capacity);
	}
	
	public TSyncInt21TripleObjectHashMap(TSyncLongObjectMap<K> map) {
		this.map = map;
	}
	
	public boolean remove(int x, int y, int z, K value) {
		long key = key(x, y, z);
		return ((TSyncLongObjectHashMap<K>)map).remove(key, value);
	}
	
	public K putIfAbsent(int x, int y, int z, K value) {
		long key = key(x, y, z);
		return ((TSyncLongObjectHashMap<K>)map).putIfAbsent(key, value);
	}

}
