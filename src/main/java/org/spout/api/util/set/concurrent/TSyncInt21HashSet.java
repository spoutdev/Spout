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
package org.spout.api.util.set.concurrent;

import org.spout.api.util.set.TInt21TripleHashSet;

import gnu.trove.TCollections;
import gnu.trove.set.hash.TLongHashSet;

/**
 * A synchronized version of the {@link TInt21TripleHashSet}.
 *
 * This set is backed by a read/write lock synchronised set.
 */
public class TSyncInt21HashSet extends TInt21TripleHashSet {
	/**
	 * Creates a new <code>TSyncInt21HashSet</code> instance backend by a synchronized (thread-safe) {@see TLongSet} instance with an capacity of 100 and the default load factor.
	 */
	public TSyncInt21HashSet() {
		super(TCollections.synchronizedSet(new TLongHashSet()));
	}

	/**
	 * Creates a new <code>TSyncInt21HashSet</code> instance backend by a synchronized (thread-safe) {@see TLongSet} instance with a prime capacity equal to or greater than <code>capacity</code> and with the default load factor.
	 *
	 * @param capacity an <code>int</code> value
	 */
	public TSyncInt21HashSet(int capacity) {
		super(TCollections.synchronizedSet(new TLongHashSet(capacity)));
	}
}
