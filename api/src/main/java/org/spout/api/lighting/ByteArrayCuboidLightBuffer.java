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
package org.spout.api.lighting;

import org.bouncycastle.util.Arrays;
import org.spout.api.util.cuboid.CuboidBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;

/**
 * A Cuboid Light Buffer that is used for unknown light managers to store the serialized data
 */
public class ByteArrayCuboidLightBuffer extends CuboidLightBuffer {
	
	private final byte[] data;
	
	public ByteArrayCuboidLightBuffer(ByteArrayCuboidLightBuffer buffer) {
		this(buffer.getManagerId(), buffer.baseX, buffer.baseY, buffer.baseZ, buffer.sizeX, buffer.sizeY, buffer.sizeZ, 
				buffer.data == null ? null : Arrays.copyOf(buffer.data, buffer.data.length));
	}

	public ByteArrayCuboidLightBuffer(int id, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, byte[] data) {
		super(null, id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		this.data = data;
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSource(CuboidBuffer source) {
		throw new UnsupportedOperationException();
	}
	
	public byte[] getData() {
		return data;
	}

	@Override
	public ByteArrayCuboidLightBuffer copy() {
		return new ByteArrayCuboidLightBuffer(this);
	}

	@Override
	public byte[] serialize() {
		return Arrays.copyOf(data, data.length);
	}

}
