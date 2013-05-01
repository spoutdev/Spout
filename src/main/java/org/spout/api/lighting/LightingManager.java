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

import org.spout.api.util.cuboid.ChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.cuboid.ImmutableCuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;

/**
 * A class for updating block 
 */
public abstract class LightingManager<T extends CuboidLightBuffer> {
	
	private final String name;
	private final short id;
	
	public LightingManager(String name) {
		this.name = getClass().getCanonicalName() + "_" + name.replace(' ', '_');
		this.id = (short) LightingRegistry.register(this);
	}
	
	public String getName() {
		return name;
	}

	public short getId() {
		return id;
	}

	@Override
	public String toString() {
		return "LightingManager {" + getName() + "}";
	}
	
	/**
	 * Recalculates lighting after a change to the block materials at a given set of block coordinates
	 * 
	 * @param light the light buffer to update
	 * @param material the updated block materials
	 * @param x the x coordinates of the changed blocks
	 * @param y the y coordinates of the changed blocks
	 * @param z the z coordinates of the changed blocks
	 * @param changedBlocks the number of changed blocks
	 */
	protected abstract void resolve(ChunkCuboidLightBufferWrapper<T> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] x, int[] y, int[] z, int changedBlocks);
	
	@SuppressWarnings("unchecked")
	public void resolveUnchecked(ChunkCuboidLightBufferWrapper<?> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] x, int[] y, int[] z, int changedBlocks) {
		resolve((ChunkCuboidLightBufferWrapper<T>) light, material, height, x, y, z, changedBlocks);
	}
	
	/**
	 * Calculates lighting after chunks have been generated.<br>
	 * 
	 * @param light the light buffer to update
	 * @param material the updated block materials
	 * @param bx an array of the bottom x block coordinates of the Chunks
	 * @param by an array of the bottom y block coordinates of the Chunks
	 * @param bz an array of the bottom z block coordinates of the Chunks
	 * @param initializedChunks the number of cuboids
	 */
	protected void initChunks(ChunkCuboidLightBufferWrapper<T> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] bx, int[] by, int[] bz, int initializedChunks) {
		resolveChunks(light, material, height, bx, by, bz, initializedChunks);
	}
	
	@SuppressWarnings("unchecked")
	public void initChunksUnchecked(ChunkCuboidLightBufferWrapper<?> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] bx, int[] by, int[] bz, int initializedChunks) {
		initChunks((ChunkCuboidLightBufferWrapper<T>) light, material, height, bx, by, bz, initializedChunks);
	}
	
	/**
	 * Recalculates lighting after a change to the block materials at a given set of cuboid regions.<br>
	 * <br>
	 * The coordinate given by (tx[i], ty[i], tz[i]) is not considered part of the ith cuboid, but (bx[i], by[i], bz[i]) 
	 * 
	 * @param light the light buffer to update
	 * @param material the updated block materials
	 * @param bx an array of the bottom x block coordinates of the Chunks
	 * @param by an array of the bottom y block coordinates of the Chunks
	 * @param bz an array of the bottom z block coordinates of the Chunks
	 * @param changedCuboids the number of cuboids
	 */
	protected abstract void resolveChunks(ChunkCuboidLightBufferWrapper<T> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] bx, int[] by, int[] bz, int changedCuboids);
	
	@SuppressWarnings("unchecked")
	public void resolveChunksUnchecked(ChunkCuboidLightBufferWrapper<?> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] bx, int[] by, int[] bz, int changedCuboids) {
		resolveChunks((ChunkCuboidLightBufferWrapper<T>) light, material, height, bx, by, bz, changedCuboids);
	}
	
	/**
	 * Recalculates lighting after a change to the world surface height.<br>
	 *
	 * @param hx the x coordinate of the world height change
	 * @param hz the z coordinate of the world height change
	 * @param oldHy the previous height for this column
	 * @param newHy the new height for this column
	 * @param changedColumns the number of changed columns
	 */
	protected abstract void resolveColumns(ChunkCuboidLightBufferWrapper<T> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] hx, int[] hz, int[] oldHy, int[] newHy, int changedColumns);

	@SuppressWarnings("unchecked")
	public void resolveColumnsUnchecked(ChunkCuboidLightBufferWrapper<?> light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] hx, int[] hz, int[] oldHy, int[] newHy, int changedColumns) {
		resolveColumns((ChunkCuboidLightBufferWrapper<T>) light, material, height, hx, hz, oldHy, newHy, changedColumns);
	}

	/**
	 * Creates a new light buffer for chunks which don't have a light buffer associated with this lighting manager
	 * 
	 * @param holder the holder for the new light buffer
	 * @param baseX the x coordinate of the base of the buffer
	 * @param baseY the y coordinate of the base of the buffer
	 * @param baseZ the z coordinate of the base of the buffer
	 * @param sizeX the x coordinate of the size of the buffer
	 * @param sizeY the y coordinate of the size of the buffer
	 * @param sizeZ the z coordinate of the size of the buffer
	 * @return
	 */
	public CuboidLightBuffer newLightBuffer(Modifiable holder, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		return deserialize(holder, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, null);
	}

	/**
	 * Deserializes a cuboid light buffer associated with this lighting manager
	 *
	 * @param holder the holder for the new light buffer
	 * @param baseX the x coordinate of the base of the buffer
	 * @param baseY the y coordinate of the base of the buffer
	 * @param baseZ the z coordinate of the base of the buffer
	 * @param sizeX the x coordinate of the size of the buffer
	 * @param sizeY the y coordinate of the size of the buffer
	 * @param sizeZ the z coordinate of the size of the buffer
	 * @param data
	 * @return
	 */
	public abstract CuboidLightBuffer deserialize(Modifiable holder, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, byte[] data);
	
}
