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

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutColumn;

public abstract class LightStore {
	private final SpoutChunk chunk;
	private final SpoutColumn column;

	/**
	 * Stores a short value of the sky light
	 * <p/>
	 * Note: These do not need to be thread-safe as long as only one thread (the
	 * region) is allowed to modify the values. If setters are provided, this
	 * will need to be made safe.
	 */
	protected final byte[] skyLight;
	protected final byte[] blockLight;

	LightStore(SpoutChunk chunk, SpoutColumn column, byte[] skyLight, byte[] blockLight) {
		this.chunk = chunk;
		this.column = column;
		
		if (skyLight == null) {
			this.skyLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
		} else {
			this.skyLight = skyLight;
		}
		if (blockLight == null) {
			this.blockLight = new byte[Chunk.BLOCKS.HALF_VOLUME];
		} else {
			this.blockLight = blockLight;
		}
	}

	public final SpoutChunk getChunk() {
		return chunk;
	}

	public final SpoutColumn getColumn() {
		return column;
	}

	public final byte[] copyBlockLight() {
		byte[] copy = new byte[Chunk.BLOCKS.HALF_VOLUME];
		System.arraycopy(blockLight, 0, copy, 0, blockLight.length);
		return copy;
	}

	public final byte[] copySkyLight() {
		byte[] copy = new byte[Chunk.BLOCKS.HALF_VOLUME];
		System.arraycopy(skyLight, 0, copy, 0, skyLight.length);
		return copy;
	}

	public abstract boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause);

	public byte setBlockLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		light &= 0xF;
		x &= Chunk.BLOCKS.MASK;
		y &= Chunk.BLOCKS.MASK;
		z &= Chunk.BLOCKS.MASK;

		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index = index >> 1;
			oldLight = NibblePairHashed.key1(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey1(blockLight[index], light);
		} else {
			index = index >> 1;
			oldLight = NibblePairHashed.key2(blockLight[index]);
			blockLight[index] = NibblePairHashed.setKey2(blockLight[index], light);
		}
		chunk.setModified();
		return oldLight;
	}

	public abstract boolean setSkyLight(int x, int y, int z, byte light, Cause<?> cause);

	public byte setSkyLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		light &= 0xF;
		x &= Chunk.BLOCKS.MASK;
		y &= Chunk.BLOCKS.MASK;
		z &= Chunk.BLOCKS.MASK;

		int index = getBlockIndex(x, y, z);
		byte oldLight;
		if ((index & 1) == 1) {
			index = index >> 1;
			oldLight = NibblePairHashed.key1(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey1(skyLight[index], light);
		} else {
			index = index >> 1;
			oldLight = NibblePairHashed.key2(skyLight[index]);
			skyLight[index] = NibblePairHashed.setKey2(skyLight[index], light);
		}
		chunk.setModified();
		return oldLight;
	}

	public final byte geSkyLight(int x, int y, int z) {
		int light = this.getSkyLightRaw(x, y, z) - (15 - chunk.getWorld().getSkyLight());
		return light < 0 ? (byte) 0 : (byte) light;
	}

	public final byte getSkyLightRaw(int x, int y, int z) {
		int index = getBlockIndex(x, y, z);
		return getSkyLightRaw(index);
	}

	public final byte getSkyLightRaw(int index) {
		byte light = skyLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	public final byte getBlockLightRaw(int x, int y, int z) {
		int index = getBlockIndex(x, y, z);
		return getBlockLightRaw(index);
	}

	public final byte getBlockLightRaw(int index) {
		byte light = blockLight[index >> 1];
		if ((index & 1) == 1) {
			return NibblePairHashed.key1(light);
		} else {
			return NibblePairHashed.key2(light);
		}
	}

	public abstract boolean isCalculatingLighting();

	public abstract void initLighting();

	protected final int getBlockIndex(int x, int y, int z) {
		return (y & Chunk.BLOCKS.MASK) << Chunk.BLOCKS.DOUBLE_BITS | (z & Chunk.BLOCKS.MASK) << Chunk.BLOCKS.BITS | (x & Chunk.BLOCKS.MASK);
	}
}
