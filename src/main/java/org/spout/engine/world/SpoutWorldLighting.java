package org.spout.engine.world;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import org.spout.api.Source;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.hashing.Int21TripleHashed;
import org.spout.api.util.set.TInt21TripleHashSet;

public class SpoutWorldLighting extends Thread implements Source {

	public static class Section {		
		private final TInt21TripleHashSet updates = new TInt21TripleHashSet();
		private TLongIterator iter; //temporary
		private final SpoutWorldLighting instance;

		public Section(SpoutWorldLighting instance) {
			this.instance = instance;
		}

		public void add(int x, int y, int z) {
			if (this.instance.running) {
				synchronized (updates) {
					updates.add(x, y, z);
				}
			}
		}

		public boolean transfer(TLongList list) {
			list.clear();
			synchronized (updates) {
				if (updates.isEmpty()) {
					return false;
				}
				iter = updates.iterator();
				while (iter.hasNext()) {
					list.add(iter.next());
				}
				updates.clear();
				return true;
			}
		}
	}

	/**
	 * A model representing the center block with all neighbours
	 */
	public static class DiamondLightModel {
		public final Element[] neighbours;
		public final Element center;

		public DiamondLightModel(SpoutWorld world, boolean sky) {
			this.neighbours = new Element[6];
			for (int i = 0; i < this.neighbours.length; i++) {
				BlockFace face = BlockFaces.NESWBT.get(i);
				this.neighbours[i] = sky ? new SkyElement(world, face) : new BlockElement(world, face);
			}
			this.center = sky ? new SkyElement(world, BlockFace.THIS) : new BlockElement(world, BlockFace.THIS);
		}

		public DiamondLightModel load(long key) {
			return this.load(Int21TripleHashed.key1(key), Int21TripleHashed.key2(key), Int21TripleHashed.key3(key));
		}

		public DiamondLightModel load(int x, int y, int z) {
			System.out.println("LOAD: " + x + "/" + y + "/" + z);
			this.center.load(x, y, z);
			for (Element element : this.neighbours) {
				element.load(x, y, z);
			}
			return this;
		}

		public void doGreater() {
			for (Element element : this.neighbours) {
				if (element.material != null) {
					if (!center.material.occludes(element.offset)) {
						if (!element.material.occludes(element.offset.getOpposite())) {
							if (center.lightToNeighbours > element.light) {
								element.setLight(center.lightToNeighbours);
							}
						}
					}
				}
			}
		}

		public static class BlockElement extends Element {
			private byte blockLight;

			public BlockElement(SpoutWorld world, BlockFace offset) {
				super(world, offset);
			}

			@Override
			public void loadLight() {
				this.blockLight = this.material.getLightLevel(this.data);
				this.light = this.chunk.getBlockLight(x, y, z);
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
			public SkyElement(SpoutWorld world, BlockFace offset) {
				super(world, offset);
			}

			@Override
			public void loadLight() {
				this.light = this.chunk.getBlockSkyLight(x, y, z);
			}

			@Override
			public void setLight(byte light) {
				if (this.light != 15) {
					System.out.println("[" + this.x + "/" + this.y + "/" + this.z + "] = " + light);
					this.light = light;
					this.chunk.setBlockSkyLight(this.x, this.y, this.z, light, this.world);
				}
			}
		}

		public static abstract class Element {
			public int x, y, z;
			public SpoutChunk chunk;
			public BlockMaterial material;
			public short data;
			public byte light;
			public byte lightToNeighbours;
			public BlockFace offset;
			public final SpoutWorld world;

			public Element(SpoutWorld world, BlockFace offset) {
				this.offset = offset;
				this.world = world;
			}

			public void load(int x, int y, int z) {
				this.x = x + (int) this.offset.getOffset().getX();
				this.y = y + (int) this.offset.getOffset().getY();
				this.z = z + (int) this.offset.getOffset().getZ();
				this.chunk = this.world.getChunkFromBlock(x, y, z, false);
				if (this.chunk == null || !this.chunk.isLoaded()) {
					this.material = null;
				} else {
					this.material = this.chunk.getBlockMaterial(x, y, z);
					this.data = this.chunk.getBlockData(x, y, z);
					this.material = this.material.getSubMaterial(this.data);
					this.lightToNeighbours = (byte) (this.light - this.material.getOpacity() - 1);
					if (this.lightToNeighbours < 0) {
						this.lightToNeighbours = 0;
					}
					this.loadLight();
				}
			}

			public abstract void loadLight();

			public abstract void setLight(byte light);
		}
	}

	public final Section skyLightGreater = new Section(this);
	public final Section skyLightLesser = new Section(this);
	public final Section blockLightGreater = new Section(this);
	public final Section blockLightLesser = new Section(this);
	private final SpoutWorld world;
	private boolean running = true;

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		this.world = world;
	}

	public void abort() {
		this.running = false;
	}

	@Override
	public void run() {
		this.running = false; //TODO: For now block light smoothing is DISABLED until it is stable enough.
		TLongList updates = new TLongArrayList();
		TLongIterator iter;
		DiamondLightModel skyModel = new DiamondLightModel(this.world, true);
		DiamondLightModel blockModel = new DiamondLightModel(this.world, false);
		while (this.running) {
			//Perform greatening sky light
			if (this.skyLightGreater.transfer(updates)) {
				iter = updates.iterator();
				while (iter.hasNext()) {
					skyModel.load(iter.next()).doGreater();
				}
			}
			//Perform greatening block light
			if (this.blockLightGreater.transfer(updates)) {
				iter = updates.iterator();
				while (iter.hasNext()) {
					blockModel.load(iter.next()).doGreater();
				}
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {}
		}
	}
}
