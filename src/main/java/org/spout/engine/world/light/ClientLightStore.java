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
package org.spout.engine.world.light;

import java.util.Arrays;

import org.spout.api.event.Cause;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutColumn;

public final class ClientLightStore extends LightStore{

	public ClientLightStore(SpoutChunk chunk, SpoutColumn column, byte[] skyLight, byte[] blockLight) {
		super(chunk, column, skyLight, blockLight);
	}
	
	@Override
	public void initLighting() {
		Arrays.fill(this.blockLight, (byte) 5);
		Arrays.fill(this.skyLight, (byte) 5);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setSkyLight(int x, int y, int z, byte light, Cause<?> cause) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCalculatingLighting() {
		return false;
	}
}
