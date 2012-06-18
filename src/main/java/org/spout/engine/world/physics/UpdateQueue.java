/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.world.physics;

import gnu.trove.list.array.TByteArrayList;

import java.util.ArrayList;

import org.spout.api.Source;
import org.spout.api.util.map.TByteShortByteKeyedHashSet;

public class UpdateQueue {

	private final TByteShortByteKeyedHashSet set = new TByteShortByteKeyedHashSet();
	private final TByteArrayList xArray = new TByteArrayList();
	private final TByteArrayList yArray = new TByteArrayList();
	private final TByteArrayList zArray = new TByteArrayList();
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private int y;
	private int z;
	private Source source;

	public void add(int x, int y, int z, Source source) {
		if (set.add(x,  y & 0xFF, z)) {
			xArray.add((byte) x);
			yArray.add((byte) y);
			zArray.add((byte) z);
			sources.add(source);
		}
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
		source = sources.remove(index);
		if (!set.remove(x, y, z)) {
			throw new IllegalStateException("Removed update location was not in HashSet");
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
	 * Gets the source
	 * 
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}
	
	

}
