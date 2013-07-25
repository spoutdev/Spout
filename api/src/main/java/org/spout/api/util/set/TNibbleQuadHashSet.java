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
package org.spout.api.util.set;

import org.spout.api.util.hashing.NibbleQuadHashed;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import gnu.trove.set.hash.TShortHashSet;

/**
 * A simplistic map that supports a 4 nibbles (4 bits) for keys, using a trove
 * short hashset in the backend.
 */
public class TNibbleQuadHashSet {
	protected TShortSet set;

	public TNibbleQuadHashSet() {
		this(17);
	}

	public TNibbleQuadHashSet(int capacity) {
		set = new TShortHashSet(capacity);
	}

	public TNibbleQuadHashSet(TShortSet set) {
		this.set = set;
	}

	public boolean add(int key1, int key2, int key3, int key4) {
		return set.add(NibbleQuadHashed.key(key1, key2, key3, key4));
	}

	public boolean contains(int key1, int key2, int key3, int key4) {
		return set.contains(NibbleQuadHashed.key(key1, key2, key3, key4));
	}

	public void clear() {
		set = new TShortHashSet(17);
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public TShortIterator iterator() {
		return set.iterator();
	}

	public boolean remove(int key1, int key2, int key3, int key4) {
		return set.remove(NibbleQuadHashed.key(key1, key2, key3, key4));
	}

	public int size() {
		return set.size();
	}

	public short[] toArray() {
		return set.toArray();
	}
}
