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
package org.spout.engine.world;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.Spout;
import org.spout.api.component.BlockComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.datatable.ManagedMap;
import org.spout.api.event.Cause;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;

public class SpoutBlock implements Block {
	private final int x, y, z;
	private final WeakReference<? extends SpoutWorld> world;
	private final AtomicReference<WeakReference<SpoutChunk>> chunk;

	public SpoutBlock(SpoutWorld world, int x, int y, int z) {
		this(world, x, y, z, null);
	}

	protected SpoutBlock(SpoutWorld world, int x, int y, int z, SpoutChunk chunk) {
		this.x = x;
		this.y = y;
		this.z = z;
		if (world != null) {
			this.world = world.getWeakReference();
		} else {
			this.world = SpoutWorld.NULL_WEAK_REFERENCE;
		}
		if (chunk != null && !chunk.containsBlock(this.x, this.y, this.z)) {
			if (chunk.getRegion().containsBlock(this.x, this.y, this.z)) {
				chunk = chunk.getRegion().getChunkFromBlock(this.x, this.y, this.z, LoadOption.NO_LOAD);
			} else {
				chunk = null; // chunk does not contain this Block, invalidate
			}
		}
		if (chunk != null) {
			this.chunk = new AtomicReference<>(chunk.getWeakReference());
		} else {
			this.chunk = new AtomicReference<>(SpoutChunk.NULL_WEAK_REFERENCE);
		}
	}

	private SpoutChunk loadChunk() {
		return getWorld().getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public Point getPosition() {
		return new Point(getWorld(), this.x + 0.5f, this.y + 0.5f, this.z + 0.5f);
	}

	@Override
	public SpoutChunk getChunk() {
		WeakReference<SpoutChunk> chunkRef = this.chunk.get();
		SpoutChunk chunk = chunkRef.get();
		if (chunk == null || !chunk.isLoaded()) {
			chunk = loadChunk();
			if (chunk == null) {
				Spout.getLogger().info("Warning: unable to load chunk for block " + this);
				this.chunk.set(SpoutChunk.NULL_WEAK_REFERENCE);
			} else {
				this.chunk.set(chunk.getWeakReference());
			}
		}
		return chunk;
	}

	@Override
	public SpoutWorld getWorld() {
		SpoutWorld world = this.world.get();
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
		if (offset == null) {
			return null;
		}
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
		return new SpoutBlock(getWorld(), this.x + dx, this.y + dy, this.z + dz, this.chunk.get().get());
	}

	@Override
	public Block getSurface() {
		int height = getWorld().getSurfaceHeight(this.x, this.z, LoadOption.LOAD_GEN);
		if (height == this.y) {
			return this;
		} else {
			return new SpoutBlock(getWorld(), this.x, height, this.z);
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
		return this.y >= getWorld().getSurfaceHeight(this.x, this.z, LoadOption.LOAD_GEN);
	}

	@Override
	public String toString() {
		return StringUtil.toNamedString(this, this.world.get(), this.x, this.y, this.z);
	}

	@Override
	public boolean setMaterial(BlockMaterial material, int data, Cause<?> cause) {
		return this.getChunk().setBlockMaterial(this.x, y, z, material, (short) data, cause);
	}

	@Override
	public boolean setMaterial(BlockMaterial material, int data) {
		return setMaterial(material, data, null);
	}

	@Override
	public SpoutBlock setData(BlockMaterial data) {
		return this.setData(data.getData());
	}

	@Override
	public SpoutBlock setData(int data) {
		return setData(data, null);
	}

	@Override
	public SpoutBlock setData(int data, Cause<?> cause) {
		this.getChunk().setBlockData(this.x, this.y, this.z, (short) data, cause);
		return this;
	}

	@Override
	public SpoutBlock addData(int data) {
		this.getChunk().addBlockData(this.x, this.y, this.z, (short) data, null);
		return this;
	}

	@Override
	public short getBlockData() {
		return this.getChunk().getBlockData(this.x, this.y, this.z);
	}

	@Override
	public short setDataBits(int bits) {
		return this.getChunk().setBlockDataBits(this.x, this.y, this.z, bits, null);
	}

	@Override
	public short setDataBits(int bits, boolean set) {
		return this.getChunk().setBlockDataBits(this.x, this.y, this.z, bits, set, null);
	}

	@Override
	public short clearDataBits(int bits) {
		return this.getChunk().clearBlockDataBits(this.x, this.y, this.z, bits, null);
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
		return this.getChunk().setBlockDataField(this.x, this.y, this.z, bits, value, null);
	}

	@Override
	public int addDataField(int bits, int value) {
		return this.getChunk().addBlockDataField(this.x, this.y, this.z, bits, value, null);
	}

	@Override
	public SpoutRegion getRegion() {
		return this.getChunk().getRegion();
	}

	@Override
	public BlockMaterial getMaterial() {
		return this.getChunk().getBlockMaterial(this.x, this.y, this.z);
	}

	@Override
	public boolean setMaterial(BlockMaterial material) {
		return this.setMaterial(material, material.getData());
	}

	@Override
	public boolean setMaterial(BlockMaterial material, Cause<?> cause) {
		return this.setMaterial(material, material.getData(), cause);
	}

	@Override
	public Block queueUpdate(EffectRange range) {
		getWorld().queueBlockPhysics(this.x, this.y, this.z, range);
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
	public DynamicUpdateEntry dynamicUpdate(boolean exclusive) {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z, exclusive);
	}

	@Override
	public DynamicUpdateEntry dynamicUpdate(long updateTime, boolean exclusive) {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z, updateTime, exclusive);
	}

	@Override
	public DynamicUpdateEntry dynamicUpdate(long updateTime, int data, boolean exclusive) {
		return this.getRegion().queueDynamicUpdate(this.x, this.y, this.z, updateTime, data, exclusive);
	}

	@Override
	public boolean isMaterial(Material... materials) {
		return getMaterial().isMaterial(materials);
	}

	@Override
	public <T extends Component> T add(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).add(type);
	}

	@Override
	public <T extends Component> T detach(Class<? extends Component> type) {
		BlockComponentOwner owner = getChunk().getBlockComponentOwner(x, y, z, false);
		if (owner != null) {
			return owner.detach(type);
		}
		return null;
	}

	@Override
	public Collection<Component> values() {
		return getChunk().getBlockComponentOwner(x, y, z, true).values();
	}

	@Override
	public ManagedMap getData() {
		BlockComponentOwner owner = getChunk().getBlockComponentOwner(x, y, z, false);
		if (owner == null) {
			throw new IllegalStateException("The datatable is only available on blocks who have a BlockComponentOwner (blocks with components added)");
		}
		return owner.getData();
	}

	@Override
	public <T extends Component> T get(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).get(type);
	}

	@Override
	public <T> T getType(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).getType(type);
	}

	@Override
	public <T extends Component> T getExact(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).getExact(type);
	}

	@Override
	public <T extends Component> Collection<T> getAll(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).getAll(type);
	}

	@Override
	public <T> Collection<T> getAllOfType(Class<T> type) {
		return getChunk().getBlockComponentOwner(x, y, z, true).getAllOfType(type);
	}
}
