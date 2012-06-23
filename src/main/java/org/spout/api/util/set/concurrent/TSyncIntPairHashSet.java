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

import org.spout.api.util.set.TIntPairHashSet;

/**
 * A synchronized set that supports 2 integers for keys, using a trove
 * long hashset in the backend.
 *
 * This map is backed by a read/write lock synchronised map.
 *
 * @param <K> the value type
 */
public class TSyncIntPairHashSet extends TIntPairHashSet {
	/**
	 * Creates a new <code>TSyncIntPairObjectHashMap</code> instance backend by a synchronized (thread-safe) {@see TSyncLongObjectHashMap} instance with an capacity of 100 and the default load factor.
	 */
	public TSyncIntPairHashSet() {
		this(100);
	}

	/**
	 * Creates a new <code>TSyncIntPairObjectHashMap</code> instance backend by a synchronized (thread-safe) {@see TSyncLongObjectHashMap} instance with a prime capacity equal to or greater than <code>capacity</code> and with the default load factor.
	 *
	 * @param capacity an <code>int</code> value
	 */
	public TSyncIntPairHashSet(int capacity) {
		super(new TSyncLongHashSet(capacity));
	}

}
