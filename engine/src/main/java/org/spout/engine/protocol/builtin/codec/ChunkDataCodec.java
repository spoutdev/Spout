/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.engine.protocol.builtin.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufs;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.util.ByteBufUtils;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;

/**
 *
 */
public class ChunkDataCodec extends MessageCodec<ChunkDataMessage> {
	private static final byte ISUNLOAD = 0b1;
	private static final byte HASBIOMES = 0b10;
	private static final int INTIAL_DATA_SIZE = Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.VOLUME * 2; // Block Ids, Block Data

	public ChunkDataCodec(int opcode) {
		super(ChunkDataMessage.class, opcode);
	}

	@Override
	public ByteBuf encode(ChunkDataMessage message) throws IOException {
		final ByteBuf buffer;
		if (message.isUnload()) {
			buffer = ByteBufs.buffer(13);
			buffer.writeByte(ISUNLOAD); // we're unloading
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
		} else {
			int size = 17; // 1 byte + 5 ints (4 byte each)
			final short lightSize = (short) message.getLight().size();
			int dataSize = INTIAL_DATA_SIZE;
			boolean hasBiomes = message.hasBiomes();
			if (hasBiomes) {
				dataSize += Chunk.BLOCKS.AREA;
			}
			dataSize += lightSize * (2 + 2048); // One short id + 1 16^3/2 chunk data per lighting manager

			byte[] uncompressedData = new byte[dataSize];
			byte[] compressedData = new byte[dataSize];

			int index = 0;
			for (short s : message.getBlockIds()) {
				uncompressedData[index++] = (byte) s;
				uncompressedData[index++] = (byte) (s >> 8);
			}
			for (short s : message.getBlockData()) {
				uncompressedData[index++] = (byte) s;
				uncompressedData[index++] = (byte) (s >> 8);
			}
			for (Entry<Short, byte[]> e : message.getLight().entrySet()) {
				short s = e.getKey();
				uncompressedData[index++] = (byte) s;
				uncompressedData[index++] = (byte) (s >> 8);
				System.arraycopy(e.getValue(), 0, uncompressedData, index, e.getValue().length);
				index += e.getValue().length;
			}
			if (hasBiomes) {
				System.arraycopy(message.getBiomeData(), 0, uncompressedData, index, message.getBiomeData().length);
				index += message.getBiomeData().length;
			}

			Deflater deflater = new Deflater();
			deflater.setInput(uncompressedData);
			deflater.finish();
			int compressedSize = deflater.deflate(compressedData);
			deflater.end();

			if (compressedSize == 0) {
				throw new IOException("Not all data compressed!");
			}

			size += compressedSize;
			buffer = ByteBufs.dynamicBuffer(size);
			buffer.writeByte(hasBiomes ? HASBIOMES : 0); // Has biomes only, not unload
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
			if (hasBiomes) {
				ByteBufUtils.writeString(buffer, message.getBiomeManagerClass());
			}
			buffer.writeShort(lightSize);
			buffer.writeInt(compressedSize);
			buffer.writeBytes(compressedData, 0, compressedSize);
		}
		return buffer;
	}

	@Override
	public ChunkDataMessage decode(ByteBuf buffer) throws IOException {
		final byte info = buffer.readByte();
		final boolean unload = (info & ISUNLOAD) == ISUNLOAD;
		final boolean hasBiomes = (info & HASBIOMES) == HASBIOMES;
		final int x = buffer.readInt();
		final int y = buffer.readInt();
		final int z = buffer.readInt();
		if (unload) {
			return new ChunkDataMessage(x, y, z);
		} else {
			final String biomeManagerClass = hasBiomes ? ByteBufUtils.readString(buffer) : null;
			final short lightSize = buffer.readShort();
			int uncompressedSize = INTIAL_DATA_SIZE;
			if (hasBiomes) {
				uncompressedSize += Chunk.BLOCKS.AREA;
			}
			uncompressedSize += lightSize * (2 + 2048); // One short id + 1 16^3/2 chunk data for every lighting manager
			final byte[] uncompressedData = new byte[uncompressedSize];
			final byte[] compressedData = new byte[buffer.readInt()];
			buffer.readBytes(compressedData);
			Inflater inflater = new Inflater();
			inflater.setInput(compressedData);
			try {
				inflater.inflate(uncompressedData);
			} catch (DataFormatException e) {
				throw new IOException("Error while reading chunk (" + x + "," + y + "," + z + ")!", e);
			}
			inflater.end();

			final short[] blockIds = new short[Chunk.BLOCKS.VOLUME];
			final short[] blockData = new short[Chunk.BLOCKS.VOLUME];
			final Map<Short, byte[]> light = new HashMap<>();
			final byte[] biomeData = hasBiomes ? new byte[Chunk.BLOCKS.AREA] : null;

			int index = 0;
			for (int i = 0; i < blockIds.length; ++i) {
				blockIds[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			for (int i = 0; i < blockData.length; ++i) {
				blockData[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			for (int i = 0; i < lightSize; ++i) {
				byte[] data = new byte[2048];
				final short lightId = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
				System.arraycopy(uncompressedData, index, data, 0, data.length);
				index += data.length;
				light.put(lightId, data);
			}
			if (hasBiomes) {
				System.arraycopy(uncompressedData, index, biomeData, 0, biomeData.length);
				index += biomeData.length;
			}

			if (index != uncompressedData.length) {
				String message = "Incorrect parse size - actual:" + index + " expected: " + uncompressedData.length;
				Spout.getLogger().severe(message);
				throw new IllegalStateException(message);
			}

			return new ChunkDataMessage(x, y, z, blockIds, blockData, biomeData, biomeManagerClass, light);
		}
	}
}
