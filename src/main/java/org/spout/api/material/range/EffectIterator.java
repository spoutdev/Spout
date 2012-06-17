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
package org.spout.api.material.range;

import java.util.Iterator;
import java.util.List;

import org.spout.api.math.IntVector3;
import org.spout.api.util.CubicIterator;
import org.spout.api.util.OutwardIterator;

public class EffectIterator implements Iterator<IntVector3> {
	
	private final OutwardIterator oi = new OutwardIterator();
	private final CubicIterator ci = new CubicIterator();
	private Iterator<IntVector3> itr = null;
	private List<IntVector3> offsetList = null;
	private int index;
	
	protected void reset() {
		offsetList = null;
		itr = null;
		ci.reset(0, 0, 0, 0);
		oi.reset(0, 0, 0);
	}
	
	protected void resetAsOutwardIterator(int range) {
		oi.reset(0, 0, 0, range);
		itr = oi;
		offsetList = null;
	}
	
	protected void resetAsCubicIterator(int range) {
		ci.reset(0, 0, 0, range);
		itr = ci;
		offsetList = null;
	}
	
	protected void resetAsCubicInterator(int bx, int by, int bz, int tx, int ty, int tz) {
		ci.reset(bx, by, bz, tx, ty, tz);
		itr = ci;
		offsetList = null;
	}
	
	protected void resetAsList(List<IntVector3> list) {
		index = 0;
		offsetList = list;
	}

	@Override
	public boolean hasNext() {
		if (offsetList != null) { 
			return index < offsetList.size();
		} else {
			return itr.hasNext();
		}
	}

	@Override
	public IntVector3 next() {
		if (offsetList != null) {
			return offsetList.get(index++);
		} else {
			return itr.next();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This method is not supported");
	}
	
	

}
