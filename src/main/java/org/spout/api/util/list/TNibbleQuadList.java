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
package org.spout.api.util.list;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.list.TShortList;
import gnu.trove.list.array.TShortArrayList;

import org.spout.api.util.hashing.NibbleQuadHashed;

/**
 * A simplistic map that supports a 4 nibbles (4 bits) for keys, using a trove
 * short hashset in the backend.
 */
public class TNibbleQuadList {
	protected final TShortList list;

	public TNibbleQuadList() {
		list = new TShortArrayList(100);
	}

	public TNibbleQuadList(int capacity) {
		list = new TShortArrayList(capacity);
	}

	public TNibbleQuadList(TShortList list) {
		this.list = list;
	}

	public boolean add(int key1, int key2, int key3, int key4) {
		return list.add(NibbleQuadHashed.key(key1, key2, key3, key4));
	}
	
	public boolean addRaw(short value) {
		return list.add(value);
	}

	public boolean contains(int key1, int key2, int key3, int key4) {
		return list.contains(NibbleQuadHashed.key(key1, key2, key3, key4));
	}

	public void clear() {
		list.clear();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public TShortIterator iterator() {
		return list.iterator();
	}

	public boolean remove(int key1, int key2, int key3, int key4) {
		return list.remove(NibbleQuadHashed.key(key1, key2, key3, key4));
	}

	public void trimToSize() {
		if (list instanceof TShortArrayList) {
			((TShortArrayList) list).trimToSize();
		}
	}
	
	public int size() {
		return list.size();
	}

	public short[] toArray() {
		return list.toArray();
	}
}
