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

import org.spout.api.Source;
import org.spout.api.material.range.EffectRange;

public class PhysicsUpdate {

	private final byte x;
	private final byte y;
	private final byte z;
	private final EffectRange range;
	private final Source source;
	
	public PhysicsUpdate(int x, int y, int z, EffectRange range, Source source) {
		this.source = source;
		this.x = (byte)x;
		this.y = (byte)y;
		this.z = (byte)z;
		this.range = range;
	}
	
	public int getX() {
		return x & 0xFF;
	}
	
	public int getY() {
		return y & 0xFF;
	}
	
	public int getZ() {
		return z & 0xFF;
	}
	
	public Source getSource() {
		return source;
	}
	
	public EffectRange getRange() {
		return range;
	}
	
}
