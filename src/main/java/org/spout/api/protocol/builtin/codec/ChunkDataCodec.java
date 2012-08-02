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

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.message.ChunkDataMessage;

/**
 *
 */
public class ChunkDataCodec extends MessageCodec<ChunkDataMessage> {
	public ChunkDataCodec() {
		super(ChunkDataMessage.class, 0x08);
	}

	@Override
	public ChannelBuffer encode(ChunkDataMessage message) throws IOException {
		final ChannelBuffer buffer;
		if (message.isUnload()) {
			buffer = ChannelBuffers.buffer(13);
			buffer.writeByte(1); // we're unloading
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
		} else {
			int size = 18;
			int dataSize = Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.HALF_VOLUME + Chunk.BLOCKS.HALF_VOLUME;
			if (message.getBiomeData() != null) {
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
			System.arraycopy(message.getBlockLight(), 0, uncompressedData, index, message.getBlockLight().length);
			index += message.getBlockLight().length;
			System.arraycopy(message.getSkyLight(), 0, uncompressedData, index, message.getSkyLight().length);
			index += message.getSkyLight().length;
			if (message.getBiomeData() != null) {
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
				size += compressedSize;
				deflater.end();
			}
			//System.out.println("Size: " + size + ", UncompressedData: " + uncompressedData.length + ", CompressedData: " + compressedSize);

			buffer = ChannelBuffers.dynamicBuffer(size);
			buffer.writeByte(0); // not unload
			buffer.writeInt(message.getX());
			buffer.writeInt(message.getY());
			buffer.writeInt(message.getZ());
			buffer.writeByte(message.getBiomeData() != null ? 1 : 0); // hasBiomes
			buffer.writeInt(compressedSize);
			buffer.writeBytes(compressedData, 0, compressedSize);
		}
		return buffer;
	}

	@Override
	public ChunkDataMessage decode(ChannelBuffer buffer) throws IOException {
		final boolean unload = buffer.readByte() == 1;
		final int x = buffer.readInt();
		final int y = buffer.readInt();
		final int z = buffer.readInt();
		if (unload) {
			return new ChunkDataMessage(x, y, z);
		} else {
			int uncompressedSize = Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.VOLUME * 2 + Chunk.BLOCKS.HALF_VOLUME + Chunk.BLOCKS.HALF_VOLUME;
			final boolean hasBiomes = buffer.readByte() == 1;
			if (hasBiomes) {
				uncompressedSize += Chunk.BLOCKS.AREA;
			}

			byte[] uncompressedData = new byte[uncompressedSize];
			byte[] compressedData = new byte[buffer.readInt()];
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
			final byte[] blockLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
			final byte[] skyLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
			final byte[] biomeData = hasBiomes ? new byte[Chunk.BLOCKS.AREA] : null;

			int index = 0;
			for (int i = 0; i < blockIds.length; ++i) {
				blockIds[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			for (int i = 0; i < blockData.length; ++i) {
				blockData[i] = (short) (uncompressedData[index++] | (uncompressedData[index++] << 8));
			}
			System.arraycopy(uncompressedData, index, blockLight, 0, blockLight.length);
			index += blockLight.length;
			System.arraycopy(uncompressedData, index, skyLight, 0, skyLight.length);
			index += skyLight.length;
			if (hasBiomes) {
				System.arraycopy(uncompressedData, index, biomeData, 0, biomeData.length);
			}

			return new ChunkDataMessage(x, y, z, blockIds, blockData, blockLight, skyLight, biomeData);
		}
	}
}
