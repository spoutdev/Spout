/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.engine.world;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.hashing.Int21TripleHashed;
import org.spout.api.util.set.TInt21TripleHashSet;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

/**
 * This model can store a diamond-shaped model of blocks to perform lighting on.<br>
 * In addition, it also stores the lists to operate on.
 */
public class SpoutWorldLightingModel {
	//TODO: Maybe merge these three into one map?
	private final TInt21TripleHashSet refresh = new TInt21TripleHashSet();
	private final TInt21TripleHashSet lesser = new TInt21TripleHashSet();
	private final TInt21TripleHashSet greater = new TInt21TripleHashSet();
	private TLongIterator iter; //temporary
	private final SpoutWorldLighting instance;
	private final TLongList updates = new TLongArrayList();

	/**
	 * The maximum amount of resolves performed per operation per tick
	 */
	public static final int MAX_PER_TICK = 200;

	public SpoutWorldLightingModel(SpoutWorldLighting instance, boolean sky) {
		this.instance = instance;
		SpoutWorld world = instance.getWorld();
		this.center = sky ? new SkyElement(world, BlockFace.THIS, this.center) : new BlockElement(world, BlockFace.THIS, this.center);
		this.neighbors = new Element[6];
		for (int i = 0; i < this.neighbors.length; i++) {
			BlockFace face = BlockFaces.NESWBT.get(i);
			this.neighbors[i] = sky ? new SkyElement(world, face, this.center) : new BlockElement(world, face, this.center);
		}
	}

	public void addRefresh(int x, int y, int z) {
		if (this.instance.isRunning()) {
			synchronized (refresh) {
				refresh.add(x, y, z);
			}
		}
	}

	public void addLesser(int x, int y, int z) {
		if (this.instance.isRunning()) {
			synchronized (lesser) {
				lesser.add(x, y, z);
			}
		}
	}

	public void addGreater(int x, int y, int z) {
		if (this.instance.isRunning()) {
			synchronized (greater) {
				greater.add(x, y, z);
			}
		}
	}

	public boolean load(TInt21TripleHashSet coordinates) {
		this.updates.clear();
		synchronized (coordinates) {
			if (coordinates.isEmpty()) {
				return false;
			}
			iter = coordinates.iterator();
			while (iter.hasNext()) {
				this.updates.add(iter.next());
			}
			coordinates.clear();
			return true;
		}
	}

	/*
	 * Routines to perform lighting updates
	 */
	public void resolve() {
		long key;
		int x, y, z, i;
		//Lesser
		for (i = 0; i < MAX_PER_TICK && this.load(greater); i++) {
			iter = this.updates.iterator();
			while (iter.hasNext()) {
				key = iter.next();
				x = Int21TripleHashed.key1(key);
				y = Int21TripleHashed.key2(key);
				z = Int21TripleHashed.key3(key);
				this.resolveGreater(x, y, z);
			}
		}
		//Greater
		for (i = 0; i < MAX_PER_TICK && this.load(lesser); i++) {
			iter = this.updates.iterator();
			while (iter.hasNext()) {
				key = iter.next();
				x = Int21TripleHashed.key1(key);
				y = Int21TripleHashed.key2(key);
				z = Int21TripleHashed.key3(key);
				this.resolveLesser(x + 1, y, z);
				this.resolveLesser(x - 1, y, z);
				this.resolveLesser(x, y + 1, z);
				this.resolveLesser(x, y - 1, z);
				this.resolveLesser(x, y, z + 1);
				this.resolveLesser(x, y, z - 1);
			}
		}
		//Refresh
		for (i = 0; i < MAX_PER_TICK && this.load(refresh); i++) {
			iter = this.updates.iterator();
			while (iter.hasNext()) {
				key = iter.next();
				x = Int21TripleHashed.key1(key);
				y = Int21TripleHashed.key2(key);
				z = Int21TripleHashed.key3(key);
				this.resolveRefresh(x, y, z);
			}
		}
	}

	public void resolveRefresh(int x, int y, int z) {
		this.load(x, y, z);
		for (Element element : this.neighbors) {
			if (element.isEmittingToCenter()) {
				if (element.light - center.opacity > center.light) {
					center.setLight((byte) (element.light - center.opacity));
				}
			}
		}
	}

	public void resolveGreater(int x, int y, int z) {
		this.load(x, y, z);
		for (Element element : this.neighbors) {
			if (element.isReceivingFromCenter()) {
				if (center.light - element.opacity > element.light) {
					element.setLight((byte) (center.light - element.opacity));
				}
			}
		}
	}

	public void resolveLesser(int x, int y, int z) {
		this.load(x, y, z);
		if (center.light == 15) {
			//direct source - don't even bother!
			this.addGreater(x, y, z);
		} else if (center.light > 0) {
			//check if it has a surrounding source
			for (Element element : this.neighbors) {
				if (element.isEmittingToCenter()) {
					if (element.light - center.opacity == center.light) {
						this.addGreater(x, y, z);
						return;
					}
				}
			}
			center.setLight((byte) 0);
		}
	}

	/*
	 * Beyond here is a basic block model loading system
	 */
	public final Element[] neighbors;
	public final Element center;

	public SpoutWorldLightingModel load(int x, int y, int z) {
		this.center.load(x, y, z);
		for (Element element : this.neighbors) {
			element.load(x, y, z);
		}
		return this;
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
			this.center = center;
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
			if (material == null || center.material.occludes(offset)) {
				return false;
			} else {
				return this.isSource() || !material.occludes(offset.getOpposite());
			}
		}

		/**
		 * Checks if the center can send light to this element
		 * @return
		 */
		public boolean isReceivingFromCenter() {
			if (material == null || material.occludes(offset.getOpposite())) {
				return false;
			} else {
				return center.isSource() || !center.material.occludes(offset);
			}
		}

		@Override
		public String toString() {
			return "[" + this.x + "/" + this.y + "/" + this.z + "/" + this.material.getDisplayName() + "] = " + this.light;
		}

		public void load(int x, int y, int z) {
			this.x = x + (int) this.offset.getOffset().getX();
			this.y = y + (int) this.offset.getOffset().getY();
			this.z = z + (int) this.offset.getOffset().getZ();
			this.chunk = this.world.getChunkFromBlock(this.x, this.y, this.z, false);
			if (this.chunk == null || !this.chunk.isLoaded()) {
				this.material = null;
			} else {
				this.material = this.chunk.getBlockMaterial(this.x, this.y, this.z);
				this.data = this.chunk.getBlockData(this.x, this.y, this.z);
				this.material = this.material.getSubMaterial(this.data);
				this.opacity = (byte) (this.material.getOpacity() + 1);
				this.loadLight();
			}
		}
		
		public abstract void loadLight();

		public abstract void setLight(byte light);
	}
}
