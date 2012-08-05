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
package org.spout.api.protocol.builtin.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.protocol.Message;
import org.spout.api.util.SpoutToStringStyle;

public class ChunkDataMessage extends Message {
	private final boolean unload;
	private final int x, y, z;
	private final short[] blockIds, blockData;
	private final byte[] blockLight, skyLight;
	private final byte[] biomeData;

	public ChunkDataMessage(int x, int y, int z) {
		this.unload = true;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockIds = ArrayUtils.EMPTY_SHORT_ARRAY;
		this.blockData = ArrayUtils.EMPTY_SHORT_ARRAY;
		this.blockLight = ArrayUtils.EMPTY_BYTE_ARRAY;
		this.skyLight = ArrayUtils.EMPTY_BYTE_ARRAY;
		this.biomeData = null;
	}

	public ChunkDataMessage(ChunkSnapshot snapshot) {
		this.unload = false;
		this.x = snapshot.getX();
		this.y = snapshot.getY();
		this.z = snapshot.getZ();
		this.blockIds = snapshot.getBlockIds();
		this.blockData = snapshot.getBlockData();
		this.blockLight = snapshot.getBlockLight();
		this.skyLight = snapshot.getSkyLight();
		this.biomeData = snapshot.getBiomeManager() != null ? snapshot.getBiomeManager().serialize() : null;
	}

	public ChunkDataMessage(int x, int y, int z, short[] blockIds, short[] blockData, byte[] blockLight, byte[] skyLight, byte[] biomeData) {
		this.unload = false;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockIds = blockIds;
		this.blockData = blockData;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
		this.biomeData = biomeData;
	}

	public boolean isUnload() {
		return unload;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public short[] getBlockIds() {
		return blockIds;
	}

	public short[] getBlockData() {
		return blockData;
	}

	public byte[] getBlockLight() {
		return blockLight;
	}

	public byte[] getSkyLight() {
		return skyLight;
	}

	public byte[] getBiomeData() {
		return biomeData;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("unload", unload)
				.append("x", x)
				.append("y", y)
				.append("z", z)
				.append("blockIds", blockIds, false)
				.append("blockData", blockData, false)
				.append("blockLight", blockLight, false)
				.append("skyLight", skyLight, false)
				.append("biomeData", biomeData, false)
				.toString();
	}
}
