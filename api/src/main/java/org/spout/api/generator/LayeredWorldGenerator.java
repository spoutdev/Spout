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
package org.spout.api.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;

/**
 * A world generator that generates using previously-specified layers of blocks
 */
public class LayeredWorldGenerator implements WorldGenerator {
	private List<Layer> layers = new ArrayList<>();
	private int minimum = Integer.MAX_VALUE;
	private int height = Integer.MIN_VALUE;
	private short floorid = 0, floordata = 0;

	@Override
	public void generate(CuboidBlockMaterialBuffer blockData, World world) {
		final int startY = blockData.getBase().getFloorY();
		final int endY = blockData.getTop().getFloorY();
		int y, height;
		for (Layer layer : this.layers) {
			if (layer.getTop() > startY && layer.getY() < endY) {
				y = Math.max(startY, layer.getY());
				height = Math.min(endY, layer.getTop()) - y;
				blockData.setHorizontalLayer(y, height, layer.getId(), layer.getData());
			}
		}
		// Floor layer
		if (startY < this.minimum) {
			height = Math.min(endY, this.minimum) - startY;
			blockData.setHorizontalLayer(startY, height, this.floorid, this.floordata);
		}
	}

	@Override
	public Populator[] getPopulators() {
		return new Populator[0];
	}

	@Override
	public String getName() {
		return "LayeredWorld";
	}

	/**
	 * Gets the total height of all layers
	 *
	 * @return Layer height
	 */
	public int getHeight() {
		return this.height;
	}

	@Override
	public int[][] getSurfaceHeight(World world, int chunkX, int chunkZ) {
		int[][] heightmap = new int[Chunk.BLOCKS.SIZE][Chunk.BLOCKS.SIZE];
		for (int i = 0; i < heightmap.length; i++) {
			Arrays.fill(heightmap[i], this.height - 1);
		}
		return heightmap;
	}

	/**
	 * Gets an immutable list of layers specified for this layered World Generator
	 *
	 * @return List of layers
	 */
	public List<Layer> getLayers() {
		return Collections.unmodifiableList(this.layers);
	}

	/**
	 * Sets the floor layer material, the material for below the lowest layer<br> By default this layer is full of empty material (air)
	 *
	 * @param material of the layer
	 */
	protected void setFloorLayer(BlockMaterial material) {
		this.setFloorLayer(material.getId(), material.getData());
	}

	/**
	 * Sets the floor layer material, the material for below the lowest layer<br> By default this layer is full of empty material (air)
	 *
	 * @param id of the material of the layer
	 * @param data of the layer
	 */
	protected void setFloorLayer(short id, short data) {
		this.floorid = id;
		this.floordata = data;
	}

	/**
	 * Stacks a new layer on top of a previous one<br> At least one layer added using addLayer should be defined before calling this method<br> Otherwise the y-coordinate of this layer will be incorrect
	 *
	 * @param height of the new layer
	 * @param material of the layer
	 */
	protected void stackLayer(int height, BlockMaterial material) {
		this.addLayer(this.height, height, material);
	}

	/**
	 * Stacks a new layer on top of a previous one<br> At least one layer added using addLayer should be defined before calling this method<br> Otherwise the y-coordinate of this layer will be incorrect
	 *
	 * @param height of the new layer
	 * @param id of the material of the layer
	 * @param data of the layer
	 */
	protected void stackLayer(int height, short id, short data) {
		this.addLayer(this.height, height, id, data);
	}

	/**
	 * Adds a single layer
	 *
	 * @param y - coordinate of the start of the layer
	 * @param height of the layer
	 * @param material of the layer
	 */
	protected void addLayer(int y, int height, BlockMaterial material) {
		this.addLayer(y, height, material.getId(), material.getData());
	}

	/**
	 * Adds a single layer
	 *
	 * @param y - coordinate of the start of the layer
	 * @param height of the layer
	 * @param id of the material of the layer
	 * @param data of the layer
	 */
	protected void addLayer(int y, int height, short id, short data) {
		final Layer layer = new Layer(y, height, id, data);
		this.layers.add(layer);
		this.height = Math.max(this.height, layer.getTop());
		this.minimum = Math.min(this.minimum, layer.getY());
	}

	public static class Layer {
		private final short id, data;
		private final int y, height, topy;

		public Layer(int y, int height, short id, short data) {
			this.y = y;
			this.height = height;
			this.topy = y + height;
			this.id = id;
			this.data = data;
		}

		public int getY() {
			return y;
		}

		public int getHeight() {
			return height;
		}

		public int getTop() {
			return topy;
		}

		public short getId() {
			return id;
		}

		public short getData() {
			return data;
		}
	}
}
