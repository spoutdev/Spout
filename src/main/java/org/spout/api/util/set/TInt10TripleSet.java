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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.util.hashing.Int10TripleHashed;


public class TInt10TripleSet {
	
	private final TIntHashSet set;
	private final Int10TripleHashed hash;
	
	public TInt10TripleSet(int bx, int by, int bz) {
		this(bx, by, bz, 16);
	}
	
	public TInt10TripleSet(int bx, int by, int bz, int initialCapacity) {
		hash = new Int10TripleHashed(bx, by, bz);
		set = new TIntHashSet(initialCapacity);
	}
	
	public boolean add(int x, int y, int z) {
		int key = hash.key(x, y, z);
		return set.add(key);
	}
	
	public boolean remove(int x, int y, int z) {
		int key = hash.key(x, y, z);
		return set.remove(key);
	}
	
	public void clear() {
		set.clear();
	}
	
	public TIntIterator iterator() {
		return set.iterator();
	}
	
	public boolean forEach(TInt10Procedure procedure) {
		return set.forEach(procedure.asTIntProcedure(hash));
	}
	
	public int size() {
		return set.size();
	}
	
	protected Int10TripleHashed getHash() {
		return hash;
	}

}
