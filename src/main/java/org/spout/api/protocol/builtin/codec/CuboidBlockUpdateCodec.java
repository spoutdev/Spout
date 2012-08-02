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
package org.spout.api.protocol.builtin.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.message.CuboidBlockUpdateMessage;

/**
 *
 */
public class CuboidBlockUpdateCodec extends MessageCodec<CuboidBlockUpdateMessage> {
	public CuboidBlockUpdateCodec() {
		super(CuboidBlockUpdateMessage.class, 0x0A);
	}

	@Override
	public ChannelBuffer encode(CuboidBlockUpdateMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(24 + message.getBlockTypes().length * 2 + message.getBlockData().length * 2 + message.getBlockLight().length + message.getSkyLight().length);
		buffer.writeInt(message.getMinX());
		buffer.writeInt(message.getMinY());
		buffer.writeInt(message.getMinZ());
		buffer.writeInt(message.getMaxX());
		buffer.writeInt(message.getMaxY());
		buffer.writeInt(message.getMaxZ());

		for (short s : message.getBlockTypes()) {
			buffer.writeShort(s);
		}
		for (short s : message.getBlockData()) {
			buffer.writeShort(s);
		}
		buffer.writeBytes(message.getBlockLight());
		buffer.writeBytes(message.getSkyLight());
		return buffer;
	}

	@Override
	public CuboidBlockUpdateMessage decode(ChannelBuffer buffer) {
		final int minX = buffer.readInt();
		final int minY = buffer.readInt();
		final int minZ = buffer.readInt();
		final int maxX = buffer.readInt();
		final int maxY = buffer.readInt();
		final int maxZ = buffer.readInt();
		final int sizeX = Math.abs(maxX - minX);
		final int sizeY = Math.abs(maxY - minY);
		final int sizeZ = Math.abs(maxZ - minZ);
		final int volume = sizeX * sizeY * sizeZ;
		short[] blockTypes = new short[volume];
		short[] blockData = new short[volume];
		int lightArraySize = volume / 2;
		if ((volume & 1) != 0) {
			++lightArraySize;
		}
		byte[] blockLight = new byte[lightArraySize];
		byte[] skyLight = new byte[lightArraySize];
		for (int i = 0; i < blockTypes.length; ++i) {
			blockTypes[i] = buffer.readShort();
		}

		for (int i = 0; i < blockData.length; ++i) {
			blockData[i] = buffer.readShort();
		}

		buffer.readBytes(blockLight);
		buffer.readBytes(skyLight);

		return new CuboidBlockUpdateMessage(minX, minY, minZ, maxX, maxY, maxZ, blockTypes, blockData, blockLight, skyLight);
	}
}
