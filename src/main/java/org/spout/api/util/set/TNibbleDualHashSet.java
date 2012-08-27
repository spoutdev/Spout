/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.util.set;

import org.spout.api.util.hashing.NibblePairHashed;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;

/**
 * A hash set that uses two 4-bit integers as key, backed by a byte trove
 * hashset.
 */
public class TNibbleDualHashSet extends NibblePairHashed {
	protected final TByteSet set;

	public TNibbleDualHashSet() {
		set = new TByteHashSet(100);
	}

	public TNibbleDualHashSet(int capacity) {
		set = new TByteHashSet(capacity);
	}

	public TNibbleDualHashSet(TByteSet set) {
		this.set = set;
	}

	public boolean add(int key1, int key2) {
		return set.add(key(key1, key2));
	}

	public boolean contains(int key1, int key2) {
		return set.contains(key(key1, key2));
	}

	public void clear() {
		set.clear();
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public TByteIterator iterator() {
		return set.iterator();
	}

	public boolean remove(int key1, int key2) {
		return set.remove(key(key1, key2));
	}

	public int size() {
		return set.size();
	}

	public byte[] toArray() {
		return set.toArray();
	}
}
