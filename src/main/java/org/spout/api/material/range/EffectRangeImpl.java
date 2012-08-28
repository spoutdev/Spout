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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.IntVector3;

public abstract class EffectRangeImpl implements EffectRange {

	private static int regionMax = Region.BLOCKS.SIZE - 1;

	private final int maxX;
	private final int maxY;
	private final int maxZ;
	private final int minX;
	private final int minY;
	private final int minZ;
	
	protected EffectRangeImpl(int range) {
		this(range, range, range);
	}
	
	protected EffectRangeImpl(int x, int y, int z) {
		this(x, y, z, -x, -y, -z);
	}
	
	protected EffectRangeImpl(int maxX, int maxY, int maxZ, int minX, int minY, int minZ) {
		if (maxX < minX || maxY < minY || maxZ < minZ) {
			throw new IllegalArgumentException("MaxX/Y/Z must be greater than MinX/Y/Z");
		}
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
	}

	protected EffectRangeImpl(Iterable<IntVector3> blocks) {
		this(blocks.iterator());
	}

	protected EffectRangeImpl(Iterator<IntVector3> blocks) {
		int mxx = 0;
		int mxy = 0;
		int mxz = 0;
		int mnx = 0;
		int mny = 0;
		int mnz = 0;
		boolean first = true;
		while (blocks.hasNext()) {
			IntVector3 next = blocks.next();
			int x = next.getX();
			int y = next.getY();
			int z = next.getZ();
			if (first) {
				first = false;
				mxx = x;
				mnx = x;
				mxy = y;
				mny = y;
				mxz = z;
				mnz = z;
				continue;
			}
			if (x > mxx) {
				mxx = x;
			} else if (x < mnx) {
				mnx = x;
			}
			if (z > mxz) {
				mxz = z;
			} else if (z < mnz) {
				mnz = z;
			}
			if (y > mxy) {
				mxy = y;
			} else if (y < mny) {
				mny = y;
			}
		}
		this.maxX = mxx;
		this.maxY = mxy;
		this.maxZ = mxz;
		this.minX = mnx;
		this.minY = mny;
		this.minZ = mnz;
		if (maxX < minX || maxY < minY || maxZ < minZ) {
			throw new IllegalArgumentException("MaxX/Y/Z must be greater than MinX/Y/Z");
		}
	}

	@Override
	public EffectIterator iterator() {
		EffectIterator iter = new EffectIterator();
		this.initEffectIterator(iter);
		return iter;
	}

	@Override
	public abstract void initEffectIterator(EffectIterator i);

	@Override
	public boolean isRegionLocal(int x, int y, int z) {
		x = x & Region.BLOCKS.MASK;
		y = y & Region.BLOCKS.MASK;
		z = z & Region.BLOCKS.MASK;
		return !(x + maxX > regionMax || y + maxY > regionMax || z + maxZ > regionMax || x + minX < 0 || y + minY < 0 || z + minZ < 0);
	}

	@Override
	public EffectRange translate(BlockFace face) {
		return this.translate(new IntVector3(face));
	}

	@Override
	public EffectRange translate(IntVector3 offset) {
		// Basic implementation, should be overridden if a better method is available
		List<IntVector3> blocks = new ArrayList<IntVector3>();
		EffectIterator iter = new EffectIterator();
		this.initEffectIterator(iter);
		while (iter.hasNext()) {
			IntVector3 translated = iter.next().copy();
			translated.add(offset);
			blocks.add(translated);
		}
		return new ListEffectRange(blocks, false);
	}
}
