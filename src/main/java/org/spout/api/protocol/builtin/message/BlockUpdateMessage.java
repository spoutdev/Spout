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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.protocol.Message;
import org.spout.api.util.SpoutToStringStyle;

public class BlockUpdateMessage extends Message {
	private final int x, y, z;
	private final short type, data;
	private final byte blockLight, skyLight;

	public BlockUpdateMessage(Block block) {
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.type = block.getMaterial().getId();
		this.data = block.getData();
		this.blockLight = block.getBlockLight();
		this.skyLight = block.getSkyLight();
	}

	public BlockUpdateMessage(int x, int y, int z, short type, short data, byte blockLight, byte skyLight) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.data = data;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
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

	public short getType() {
		return type;
	}

	public short getData() {
		return data;
	}

	public byte getBlockLight() {
		return blockLight;
	}

	public byte getSkyLight() {
		return skyLight;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("x", x)
				.append("y", y)
				.append("z", z)
				.append("type", type)
				.append("data", data)
				.append("blockLight", blockLight)
				.append("skyLight", skyLight)
				.toString();
	}
}
