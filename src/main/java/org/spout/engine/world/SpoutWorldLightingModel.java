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

import gnu.trove.iterator.TShortIterator;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.hashing.NibbleQuadHashed;

/**
 * This model can store a diamond-shaped model of blocks to perform lighting on.<br>
 * In addition, it also stores the lists to operate on.
 */
public class SpoutWorldLightingModel {
	private final SpoutWorldLighting instance;
	private final boolean sky;

	//Used to debug and log statistics
	private int changes = 0;
	private long lastResolveTime = 0;
	private long processTime = 0;
	private short[] updates = new short[1000];
	private int updateCount = 0;
	private TShortIterator iter;
	private SpoutChunk chunk;

	public void reportChanges() {
		if (this.changes > 1000) {
			if (Spout.debugMode()) {
				StringBuilder builder = new StringBuilder();
				builder.append("[debug] Finished processing ").append(this.changes).append(sky ? " sky" : " block");
				builder.append(" lighting operations in ").append(processTime / 1E6D).append(" ms");
				System.out.println(builder);
			}
			this.changes = 0;
			this.processTime = 0;
		}
	}

	/**
	 * The maximum amount of resolves performed per operation per tick
	 */
	public static final int MAX_PER_TICK = 200;

	public SpoutWorldLightingModel(SpoutWorldLighting instance, boolean sky) {
		this.sky = sky;
		this.instance = instance;
		SpoutWorld world = instance.getWorld();
		this.center = sky ? new SkyElement(world, BlockFace.THIS, null) : new BlockElement(world, BlockFace.THIS, null);
		this.neighbors = new Element[6];
		for (int i = 0; i < this.neighbors.length; i++) {
			BlockFace face = BlockFaces.NESWBT.get(i);
			this.neighbors[i] = sky ? new SkyElement(world, face, this.center) : new BlockElement(world, face, this.center);
		}
	}

	/**
	 * Checks if this model can greaten the lighting at the block specified
	 * @param chunk the block is in
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return True if it can greaten the lighting, False if not
	 */
	public boolean canGreater(SpoutChunk chunk, int x, int y, int z) {
		// Block lighting can always greaten, an occluded block could be a light source
		return !this.sky || !chunk.getBlockMaterial(x, y, z).getOcclusion().get(BlockFaces.NESWBT);
	}

	/**
	 * Checks if this model can refresh the lighting at the block specified
	 * @param chunk the block is in
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return True if it can refresh the lighting, False if not
	 */
	public boolean canRefresh(SpoutChunk chunk, int x, int y, int z) {
		return !chunk.getBlockMaterial(x, y, z).getOcclusion().get(BlockFaces.NESWBT);
	}

	/**
	 * Resolves all the operations in the chunk specified
	 * @param chunk in which the updates exist
	 */
	public boolean resolve(SpoutChunk chunk) {
		int x = 0, y = 0, z = 0;
		try {
			// Load chunk information
			if (this.sky) {
				synchronized (chunk.skyLightOperations) {
					this.updateCount = chunk.skyLightOperations.size();
					if (this.updateCount > 0) {
						if (this.updateCount > this.updates.length) {
							this.updates = new short[this.updateCount + 100];
						}
						this.iter = chunk.skyLightOperations.iterator();
						for (int i = 0; i < this.updateCount && this.iter.hasNext(); i++) {
							this.updates[i] = iter.next();
						}
						chunk.skyLightOperations.clear();
					} else {
						return false;
					}
				}
			} else {
				synchronized (chunk.blockLightOperations) {
					this.updateCount = chunk.blockLightOperations.size();
					if (this.updateCount > 0) {
						if (this.updateCount > this.updates.length) {
							this.updates = new short[this.updateCount + 100];
						}
						this.iter = chunk.blockLightOperations.iterator();
						for (int i = 0; i < this.updateCount && this.iter.hasNext(); i++) {
							this.updates[i] = iter.next();
						}
						chunk.blockLightOperations.clear();
					} else {
						return false;
					}
				}
			}
			this.chunk = chunk;
			this.lastResolveTime = System.nanoTime();
			int i;
			short key;

			// Greater
			for (i = 0; i < updateCount; i++) {
				key = updates[i];
				if (NibbleQuadHashed.key4(key) == SpoutWorldLighting.GREATER) {
					x = NibbleQuadHashed.key1(key) + this.chunk.getBlockX();
					y = NibbleQuadHashed.key2(key) + this.chunk.getBlockY();
					z = NibbleQuadHashed.key3(key) + this.chunk.getBlockZ();
					this.resolveGreater(x, y, z);
				}
			}

			// Lesser
			for (i = 0; i < updateCount; i++) {
				key = updates[i];
				if (NibbleQuadHashed.key4(key) == SpoutWorldLighting.LESSER) {
					x = NibbleQuadHashed.key1(key) + this.chunk.getBlockX();
					y = NibbleQuadHashed.key2(key) + this.chunk.getBlockY();
					z = NibbleQuadHashed.key3(key) + this.chunk.getBlockZ();
					this.resolveLesser(x + 1, y, z);
					this.resolveLesser(x - 1, y, z);
					this.resolveLesser(x, y + 1, z);
					this.resolveLesser(x, y - 1, z);
					this.resolveLesser(x, y, z + 1);
					this.resolveLesser(x, y, z - 1);
				}
			}

			// Refresh
			for (i = 0; i < updateCount; i++) {
				key = updates[i];
				if (NibbleQuadHashed.key4(key) == SpoutWorldLighting.REFRESH) {
					x = NibbleQuadHashed.key1(key) + this.chunk.getBlockX();
					y = NibbleQuadHashed.key2(key) + this.chunk.getBlockY();
					z = NibbleQuadHashed.key3(key) + this.chunk.getBlockZ();
					this.resolveRefresh(x, y, z);
				}
			}
		} catch (Throwable t) {
			String type = sky ? "sky" : "block";
			System.out.println("An exception occurred while resolving " + type + " lighting at block [" + x + "/" + y + "/" + z + "/" + this.instance.getWorld() + "]:");
			t.printStackTrace();
		}
		this.changes += updateCount;
		this.processTime += System.nanoTime() - lastResolveTime;
		this.chunk = null;
		return true;
	}

	public void resolveRefresh(int x, int y, int z) {
		if (this.load(x, y, z)) {
			for (Element element : this.neighbors) {
				if (element.isEmittingToCenter()) {
					if (element.light - center.opacity > center.light) {
						center.setLight((byte) (element.light - center.opacity));
					}
				}
			}
		}
	}

	public void resolveGreater(int x, int y, int z) {
		if (this.load(x, y, z)) {
			for (Element element : this.neighbors) {
				if (element.isReceivingFromCenter()) {
					if (center.light - element.opacity > element.light) {
						element.setLight((byte) (center.light - element.opacity));
					}
				}
			}
		}
	}

	public void resolveLesser(int x, int y, int z) {
		if (this.load(x, y, z)) {
			if (center.light == 15) {
				//direct source - don't even bother!
				center.addOperation(SpoutWorldLighting.GREATER);
			} else if (center.light > 0) {
				//check if it has a surrounding source
				for (Element element : this.neighbors) {
					if (element.isEmittingToCenter()) {
						if (element.light - center.opacity == center.light) {
							center.addOperation(SpoutWorldLighting.GREATER);
							return;
						}
					}
				}
				center.setLight((byte) 0);
			}
		}
	}

	/*
	 * Beyond here is a basic block model loading system
	 */
	public final Element[] neighbors;
	public final Element center;

	/**
	 * Removes all live information from this model
	 */
	public void cleanUp() {
		center.cleanUp();
		for (Element neigh: this.neighbors) {
			neigh.cleanUp();
		}
	}

	/**
	 * Loads the block model
	 * @param x coordinate of the center block
	 * @param y coordinate of the center block
	 * @param z coordinate of the center block
	 * @return True if it was successful
	 */
	public boolean load(int x, int y, int z) {
		this.center.load(x, y, z);
		if (this.center.material == null) {
			return false;
		} else if (this.center.material.getOcclusion().get(BlockFaces.NESWBT) && !this.center.isSource()) {
			return false; // Do not continue if the block occludes all faces and is not a source
		}
		for (Element element : this.neighbors) {
			element.load();
		}
		return true;
	}

	public static class BlockElement extends Element {
		private byte blockLight;

		public BlockElement(SpoutWorld world, BlockFace offset, Element center) {
			super(world, offset, center);
		}

		@Override
		public void loadLight() {
			this.blockLight = this.material.getLightLevel(this.data);
			this.light = this.chunk.getBlockLight(x, y, z);
		}

		@Override
		public boolean isSource() {
			return this.blockLight > 0;
		}

		@Override
		public void setLight(byte light) {
			this.light = light;
			if (this.light < this.blockLight) {
				this.light = this.blockLight;
			}
			this.chunk.setBlockLight(this.x, this.y, this.z, this.light, this.world);
		}

		@Override
		public void addOperation(int operation) {
			this.chunk.addBlockLightOperation(this.x, this.y, this.z, operation);
		}
	}

	public static class SkyElement extends Element {

		public SkyElement(SpoutWorld world, BlockFace offset, Element center) {
			super(world, offset, center);
		}

		@Override
		public void loadLight() {
			this.light = this.chunk.getBlockSkyLight(x, y, z);
		}

		@Override
		public void setLight(byte light) {
			if (this.light != 15) {
				this.light = light;
				this.chunk.setBlockSkyLight(this.x, this.y, this.z, light, this.world);
			}
		}

		@Override
		public void addOperation(int operation) {
			this.chunk.addSkyLightOperation(this.x, this.y, this.z, operation);
		}
	}

	/**
	 * Contains the live information of a single block
	 */
	public static abstract class Element {
		public int x, y, z;
		public SpoutChunk chunk;
		public BlockMaterial material;
		public short data;
		public byte light;
		public byte opacity;
		public BlockFace offset;
		public final SpoutWorld world;
		public final Element center;

		public Element(SpoutWorld world, BlockFace offset, Element center) {
			this.offset = offset;
			this.world = world;
			this.center = center == null ? this : center;;
		}

		/**
		 * Checks if this element is a source of light (and does not require occlusion checks)
		 */
		public boolean isSource() {
			return false;
		}
		
		/**
		 * Checks if this element can send light to the center
		 */
		public boolean isEmittingToCenter() {
			if (material == null) {
				return false;
			}

			if (center.material.getOcclusion().get(offset)) {
				return false;
			}

			return this.isSource() || !material.getOcclusion().get(offset.getOpposite());
		}

		/**
		 * Checks if the center can send light to this element
		 * @return
		 */
		public boolean isReceivingFromCenter() {
			if (material == null) {
				return false;
			}

			if (material.getOcclusion().get(offset.getOpposite())) {
				return false;
			}

			return center.isSource() || !center.material.getOcclusion().get(offset);
		}

		@Override
		public String toString() {
			return "[" + this.x + "/" + this.y + "/" + this.z + "/" + this.material.getDisplayName() + "] = " + this.light;
		}

		/**
		 * Adds a new operation for this block
		 * @param operation to perform
		 */
		public abstract void addOperation(int operation);

		/**
		 * Loads the material, data and lighting information of this element<br>
		 * This assumes this element is the center
		 * @param x coordinate of the center block element
		 * @param y coordinate of the center block element
		 * @param z coordinate of the center block element
		 */
		public void load(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.load();
		}

		/**
		 * Loads the material, data and lighting information of this element<br>
		 * This assumes this element is a neighbor
		 * @param x coordinate of the center block element
		 * @param y coordinate of the center block element
		 * @param z coordinate of the center block element
		 */
		public void load() {
			if (this.center != this) {
				if (!this.center.isSource()) {
					// Check if the center material occludes, and if so, stop loading
					if (center.material.getOcclusion().get(this.offset)) {
						this.material = null;
						return;
					}
				}
				this.x = this.center.x + (int) this.offset.getOffset().getX();
				this.y = this.center.y + (int) this.offset.getOffset().getY();
				this.z = this.center.z + (int) this.offset.getOffset().getZ();
			}
			if (center.chunk != null) {
				if (center.chunk.isLoaded() && center.chunk.containsBlock(this.x, this.y, this.z)) {
					this.chunk = center.chunk;
				} else if (center.chunk.getRegion().containsBlock(this.x, this.y, this.z)) {
					this.chunk = center.chunk.getRegion().getChunkFromBlock(this.x, this.y, this.z, LoadOption.LOAD_ONLY);
				} else {
					this.chunk = this.world.getChunkFromBlock(this.x, this.y, this.z, LoadOption.LOAD_ONLY);
				}
			} else {
				this.chunk = this.world.getChunkFromBlock(this.x, this.y, this.z, LoadOption.LOAD_ONLY);
			}
			if (this.chunk == null || !this.chunk.isLoaded()) {
				this.material = null;
			} else {
				this.material = this.chunk.getBlockMaterial(this.x, this.y, this.z);
				this.data = this.chunk.getBlockData(this.x, this.y, this.z);
				this.opacity = (byte) (this.material.getOpacity() + 1);
				this.loadLight();
			}
		}

		public void cleanUp() {
			this.chunk = null;
			this.material = null;
		}

		public abstract void loadLight();

		public abstract void setLight(byte light);
	}
}
