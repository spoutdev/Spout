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
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.util.ChannelBufferUtils;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;

/**
 *
 */
public class ChunkDataCodec extends MessageCodec<ChunkDataMessage> {
	private static final byte ISUNLOAD = 0b1;
	private static final byte HASBIOMES = 0b10;
	//private static final int INTIAL_DATA_SIZE = Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.HALF_VOLUME + Chunk.BLOCKS.HALF_VOLUME; // Block Ids, Block Data, Block light, Sky light
	private static final int INTIAL_DATA_SIZE = Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.VOLUME * 2; // Block Ids, Block Data

	public ChunkDataCodec(int opcode) {
		super(ChunkDataMessage.class, opcode);
	}

	@Override
	public ChannelBuffer encode(ChunkDataMessage message) throws IOException {
		final ChannelBuffer buffer;
		if (message.isUnload()) {
			buffer = ChannelBuffers.buffer(13);
			buffer.writeByte(ISUNLOAD); // we're unloading
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
		} else {
			int size = 17; // 1 byte + 4 ints (4 byte each)
			int dataSize = INTIAL_DATA_SIZE;
			boolean hasBiomes = message.hasBiomes();
			if (hasBiomes) {
				dataSize += Chunk.BLOCKS.AREA;
			}
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
			/*for (byte b : message.getBlockLight()) {
				uncompressedData[index++] = (byte) b;
			}
			for (byte b : message.getSkyLight()) {
				uncompressedData[index++] = (byte) b;
			}*/
			if (hasBiomes) {
				System.arraycopy(message.getBiomeData(), 0, uncompressedData, index, message.getBiomeData().length);
				index += message.getBiomeData().length;
			}

			Deflater deflater = new Deflater();
			deflater.setInput(uncompressedData);
			deflater.finish();
			int compressedSize = deflater.deflate(compressedData);
			try {
				if (compressedSize == 0) {
					throw new IOException("Not all data compressed!");
				}
			} finally {
				deflater.end();
			}

			size += compressedSize;
			buffer = ChannelBuffers.dynamicBuffer(size);
			buffer.writeByte(hasBiomes ? HASBIOMES : 0); // Has biomes only, not unload
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
			if (hasBiomes) {
				ChannelBufferUtils.writeString(buffer, message.getBiomeManagerClass());
			}
			buffer.writeInt(compressedSize);
			buffer.writeBytes(compressedData, 0, compressedSize);
		}
		return buffer;
	}

	@Override
	public ChunkDataMessage decode(ChannelBuffer buffer) throws IOException {
		final byte info = buffer.readByte();
		final boolean unload = (info & ISUNLOAD) == ISUNLOAD;
		final boolean hasBiomes = (info & HASBIOMES) == HASBIOMES;
		final int x = buffer.readInt();
		final int y = buffer.readInt();
		final int z = buffer.readInt();
		if (unload) {
			return new ChunkDataMessage(x, y, z);
		} else {
			final String biomeManagerClass = hasBiomes ? ChannelBufferUtils.readString(buffer) : null;
			int uncompressedSize = INTIAL_DATA_SIZE;
			if (hasBiomes) {
				uncompressedSize += Chunk.BLOCKS.AREA;
			}
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
			//final byte[] blockLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
			//final byte[] skyLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
			final byte[] biomeData = hasBiomes ? new byte[Chunk.BLOCKS.AREA] : null;

			int index = 0;
			for (int i = 0; i < blockIds.length; ++i) {
				blockIds[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			for (int i = 0; i < blockData.length; ++i) {
				blockData[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			/*System.arraycopy(uncompressedData, index, blockLight, 0, blockLight.length);
			index += blockLight.length;
			System.arraycopy(uncompressedData, index, skyLight, 0, skyLight.length);
			index += skyLight.length;*/
			if (hasBiomes) {
				System.arraycopy(uncompressedData, index, biomeData, 0, biomeData.length);
			}

			return new ChunkDataMessage(x, y, z, blockIds, blockData, biomeData, biomeManagerClass);
		}
	}
}
