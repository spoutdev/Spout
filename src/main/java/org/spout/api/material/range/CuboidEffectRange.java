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

import org.spout.api.math.IntVector3;

public class CuboidEffectRange extends EffectRangeImpl {

	private final int tx;
	private final int ty;
	private final int tz;
	private final int bx;
	private final int by;
	private final int bz;

	public CuboidEffectRange(IntVector3 bottom, IntVector3 top) {
		this(bottom.getX(), bottom.getY(), bottom.getZ(), top.getX(), top.getY(), top.getZ());
	}

	public CuboidEffectRange(int bx, int by, int bz, int tx, int ty, int tz) {
		super(tx, ty, tz, bx, by, bz);
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}

	@Override
	public void initEffectIterator(EffectIterator i) {
		i.resetAsCubicInterator(bx, by, bz, tx, ty, tz);
	}

	@Override
	public EffectRange translate(IntVector3 offset) {
		IntVector3 bottom = new IntVector3(this.bx, this.by, this.bz);
		IntVector3 top = new IntVector3(this.tx, this.ty, this.tz);
		bottom.add(offset);
		top.add(offset);
		return new CuboidEffectRange(bottom, top);
	}
}
