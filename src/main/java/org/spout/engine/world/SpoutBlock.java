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
package org.spout.engine.world;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.Source;
import org.spout.api.component.components.BlockComponent;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.EffectRange;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.IntVector3;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.util.LogicUtil;
import org.spout.api.util.StringUtil;

public class SpoutBlock implements Block {
	private final int x, y, z;
	private final WeakReference<World> world;
	private final Source source;
	private final AtomicReference<WeakReference<Chunk>> chunk;

	public SpoutBlock(World world, int x, int y, int z, Source source) {
		this(world, x, y, z, null, source);
	}

	protected SpoutBlock(World world, int x, int y, int z, Chunk chunk, Source source) {
		if (source == null) {
			throw new IllegalArgumentException("Every block must have a source");
		}
		this.x = x;
		this.y = y;
		this.z = z;
		if (world != null) {
			this.world = ((SpoutWorld) world).getWeakReference();
		} else {
			this.world = SpoutWorld.NULL_WEAK_REFERENCE;
		}
		this.source = source;
		if (chunk != null && !chunk.containsBlock(this.x, this.y, this.z)) {
			chunk = null; // chunk does not contain this Block, invalidate
		}
		if (chunk != null) {
			this.chunk = new AtomicReference<WeakReference<Chunk>>(((SpoutChunk) chunk).getWeakReference());
		} else {
			this.chunk = new AtomicReference<WeakReference<Chunk>>(SpoutChunk.NULL_WEAK_REFERENCE);
		}
	}

	private final Chunk loadChunk() {
		return getWorld().getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public Point getPosition() {
		return new Point(getWorld(), this.x + 0.5f, this.y + 0.5f, this.z + 0.5f);
	}

	@Override
	public Chunk getChunk() {
		WeakReference<Chunk> chunkRef = this.chunk.get();
		if (chunkRef.get() == null || !chunkRef.get().isLoaded()) {
			Chunk chunk = loadChunk();
			if (chunk == null) {
				this.chunk.set(SpoutChunk.NULL_WEAK_REFERENCE);
			} else {
				this.chunk.set(((SpoutChunk) chunk).getWeakReference());
			}
			return chunk;
		}
		return chunkRef.get();
	}

	@Override
	public World getWorld() {
		World world = this.world.get();
		if (world == null) {
			throw new IllegalStateException("The world has been unloaded!");
		}
		return world;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public Block translate(BlockFace offset, int distance) {
		return this.translate(offset.getOffset().multiply(distance));
	}

	@Override
	public Block translate(BlockFace offset) {
		return this.translate(offset.getOffset());
	}

	@Override
	public Block translate(Vector3 offset) {
		return this.translate((int) offset.getX(), (int) offset.getY(), (int) offset.getZ());
	}

	@Override
	public Block translate(IntVector3 offset) {
		return this.translate(offset.getX(), offset.getY(), offset.getZ());
	}

	@Override
	public Block translate(int dx, int dy, int dz) {
		return new SpoutBlock(getWorld(), this.x + dx, this.y + dy, this.z + dz, this.chunk.get().get(), this.source);
	}

	@Override
	public Block getSurface() {
		int height = getWorld().getSurfaceHeight(this.x, this.z, true);
		if (height == this.y) {
			return this;
		} else {
			return new SpoutBlock(getWorld(), this.x, height, this.z, source);
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (other != null && other instanceof Block) {
			Block b = (Block) other;
			return b.getWorld() == this.getWorld() && b.getX() == this.getX() && b.getY() == this.getY() && b.getZ() == this.getZ();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getWorld()).append(getX()).append(getY()).append(getZ()).toHashCode();
	}

	@Override
	public boolean isAtSurface() {
		return this.y >= getWorld().getSurfaceHeight(this.x, this.z, true);
	}

	@Override
	public String toString() {
		return StringUtil.toNamedString(this, this.world.get(), this.x, this.y, this.z);
	}

	@Override
	public SpoutBlock setMaterial(MaterialSource material, int data) {
		if (material.getMaterial() instanceof BlockMaterial) {
			this.getChunk().setBlockMaterial(this.x, y, z, (BlockMaterial) material.getMaterial(), (short) data, this.source);
		} else {
			throw new IllegalArgumentException("Can't set a block to a non-block material!");
		}
		return this;
	}

	@Override
	public SpoutBlock setData(DataSource data) {
		return this.setData(data.getData());
	}

	@Override
	public SpoutBlock setData(int data) {
		this.getChunk().setBlockData(this.x, this.y, this.z, (short) data, this.source);
		return this;
	}

	@Override
	public SpoutBlock addData(int data) {
		this.getChunk().addBlockData(this.x, this.y, this.z, (short) data, this.source);
		return this;
	}

	@Override
	public short getData() {
		return this.getChunk().getBlockData(this.x, this.y, this.z);
	}

	@Override
	public short setDataBits(int bits) {
		return this.getChunk().setBlockDataBits(this.x, this.y, this.z, bits, this.source);
	}

	@Override
	public short setDataBits(int bits, boolean set) {
		return this.getChunk().setBlockDataBits(this.x, this.y, this.z, bits, set, this.source);
	}

	@Override
	public short clearDataBits(int bits) {
		return this.getChunk().clearBlockDataBits(this.x, this.y, this.z, bits, this.source);
	}

	@Override
	public int getDataField(int bits) {
		return this.getChunk().getBlockDataField(this.x, this.y, this.z, bits);
	}

	@Override
	public boolean isDataBitSet(int bits) {
		return this.getChunk().isBlockDataBitSet(this.x, this.y, this.z, bits);
	}

	@Override
	public int setDataField(int bits, int value) {
		return this.getChunk().setBlockDataField(this.x, this.y, this.z, bits, value, this.source);
	}

	@Override
	public int addDataField(int bits, int value) {
		return this.getChunk().addBlockDataField(this.x, this.y, this.z, bits, value, this.source);
	}

	@Override
	public Source getSource() {
		return this.source;
	}

	@Override
	public Region getRegion() {
		return this.getChunk().getRegion();
	}

	@Override
	public BlockMaterial getMaterial() {
		return this.getChunk().getBlockMaterial(this.x, this.y, this.z);
	}

	@Override
	public Block setMaterial(MaterialSource material) {
		return this.setMaterial(material, material.getData());
	}

	@Override
	public Block setMaterial(MaterialSource material, DataSource data) {
		return this.setMaterial(material, data.getData());
	}

	@Override
	public byte getLight() {
		return MathHelper.max(this.getSkyLight(), this.getBlockLight());
	}

	@Override
	public Block setSkyLight(byte level) {
		this.getChunk().setBlockSkyLight(this.x, this.y, this.z, level, this.source);
		return this;
	}

	@Override
	public Block setBlockLight(byte level) {
		this.getChunk().setBlockLight(this.x, this.y, this.z, level, this.source);
		return this;
	}

	@Override
	public byte getBlockLight() {
		return this.getChunk().getBlockLight(this.x, this.y, this.z);
	}

	@Override
	public byte getSkyLight() {
		return this.getChunk().getBlockSkyLight(this.x, this.y, this.z);
	}

	@Override
	public byte getSkyLightRaw() {
		return this.getChunk().getBlockSkyLightRaw(this.x, this.y, this.z);
	}

	@Override
	public Block queueUpdate(EffectRange range) {
		getWorld().queueBlockPhysics(this.x, this.y, this.z, range, this.source);
		return this;
	}

	@Override
	public Biome getBiomeType() {
		return getWorld().getBiome(x, y, z);
	}

	@Override
	public void resetDynamic() {
		this.getRegion().resetDynamicBlock(this.x, this.y, this.z);
	}

	@Override
	public void syncResetDynamic() {
		this.getRegion().syncResetDynamicBlock(this.x, this.y, this.z);
	}

	@Override
	public DynamicUpdateEntry dynamicUpdate() {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z);
	}

	@Override
	public DynamicUpdateEntry dynamicUpdate(long updateTime) {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z, updateTime);
	}

	@Override
	public DynamicUpdateEntry dynamicUpdate(long updateTime, int data) {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z, updateTime, data);
	}

	@Override
	public boolean isMaterial(Material... materials) {
		return LogicUtil.equalsAny(this.getMaterial(), (Object[]) materials);
	}

	@Override
	public BlockComponent getComponent() {
		return this.getRegion().getBlockComponent(x, y, z);
	}
}
