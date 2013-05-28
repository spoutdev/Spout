/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.world.physics;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TByteArrayList;

import java.util.ArrayList;

import org.spout.api.material.BlockMaterial;
import org.spout.api.util.map.TByteShortByteKeyedObjectHashMap;

public class UpdateQueue {

	private final TByteShortByteKeyedObjectHashMap<TIntList> map = new TByteShortByteKeyedObjectHashMap<TIntList>();
	private final TByteArrayList xArray = new TByteArrayList();
	private final TByteArrayList yArray = new TByteArrayList();
	private final TByteArrayList zArray = new TByteArrayList();
	private final ArrayList<BlockMaterial> materials = new ArrayList<BlockMaterial>();
	private int y;
	private int z;
	private BlockMaterial oldMaterial;
	private int maxSize = 0;
	private int maxMapSize = 0;

	public void add(int x, int y, int z, BlockMaterial oldMaterial) {
		TIntList list = map.get(x, y & 0xFF, z);
		if (list != null) {
			TIntIterator i = list.iterator();
			while (i.hasNext()) {
				int index = i.next();
				if (
						(xArray.get(index) & 0xFF) == (x & 0xFF) && 
						(yArray.get(index) & 0xFF) == (y & 0xFF) && 
						(zArray.get(index) & 0xFF) == (z & 0xFF) &&
						materials.get(index) == oldMaterial
						) {
					return;
				}
			}
		} else {
			list = new TIntArrayList();
			map.put(x, y & 0xFF, z, list);
			if (map.size() > maxMapSize) {
				maxMapSize = map.size();
			}
		}
		int size = xArray.size();
		if (size > maxSize) {
			maxSize = size;
		} 
		list.add(size);
		xArray.add((byte) x);
		yArray.add((byte) y);
		zArray.add((byte) z);
		materials.add(oldMaterial);
	}

	public boolean hasNext() {
		return !xArray.isEmpty();
	}

	/**
	 * Gets the next x coordinate.  This method updates the internal array indexes and should only be called if hasNext returns true
	 * 
	 * @return the next x coordinate
	 */
	public int getX() {
		int x;
		int index = xArray.size() - 1;
		x = xArray.removeAt(index) & 0xFF;
		y = yArray.removeAt(index) & 0xFF;
		z = zArray.removeAt(index) & 0xFF;
		oldMaterial = materials.remove(index);
		
		if (maxSize > 10 && index < (maxSize >> 1)) {
			xArray.trimToSize();
			yArray.trimToSize();
			zArray.trimToSize();
			materials.trimToSize();
			maxSize = xArray.size();
		}
		
		TIntList list = map.get(x, y & 0xFF, z);
		if (list == null || !list.remove(index)) {
			throw new IllegalStateException("Index was not in list, or list was null");
		}
		if (list.isEmpty()) {
			if (map.remove(x, y & 0xFF, z) == null) {
				throw new IllegalStateException("Removed update location was not in HashSet");
			}
			if (maxMapSize > 5 && map.size() < (maxMapSize >> 1)) {
				map.compact();
				maxMapSize = map.size();
			}
		}
		return x;
	}
	
	/**
	 * Gets the y coordinate
	 * 
	 * @return the y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Gets the z coordinate
	 * 
	 * @return the z coordinate
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets the old material
	 * 
	 * @return the old material
	 */
	public BlockMaterial getOldMaterial() {
		return oldMaterial;
	}

}
