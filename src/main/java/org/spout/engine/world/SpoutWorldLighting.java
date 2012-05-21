package org.spout.engine.world;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.hash.TLongHashSet;

import java.util.LinkedList;
import java.util.Queue;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.set.TInt21TripleHashSet;
import org.spout.api.util.set.TNibbleDualHashSet;

public class SpoutWorldLighting extends Thread {

	private final SpoutWorld world;
	private boolean running = true;
	
	/**
	 * The blocks this lighting manager still has to operate on
	 */
	private final TInt21TripleHashSet dirtyBlocks = new TInt21TripleHashSet();

	/**
	 * The chunks this lighting manager still has to perform lighting updates on<br>
	 * It excludes all the block updates scheduled under 'dirtyBlocks'.
	 */
	private final Queue<SpoutChunk> dirtyChunks = new LinkedList<SpoutChunk>();

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		this.world = world;
	}

	/**
	 * Marks a block dirty for lighting updates
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 */
	public void reportDirty(int x, int y, int z) {
		synchronized (dirtyBlocks) {
			dirtyBlocks.add(x, y, z);
		}
	}

	/**
	 * Marks a sky column dirty
	 * 
	 * @param x coordinate of the block column
	 * @param z coordinate of the block column
	 */
	public void reportChunkDirty(SpoutChunk chunk) {
		synchronized (dirtyChunks) {
			dirtyChunks.offer(chunk);
		}
	}

	public void abort() {
		this.running = false;
	}

	private static final byte[] COLUMNS_ALL;
	
	static {
		COLUMNS_ALL = new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
		int i = 0;
		for (byte x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (byte z = 0; z < Chunk.CHUNK_SIZE; z++) {
				COLUMNS_ALL[i++] = TNibbleDualHashSet.key(x, z);
			}
		}
	}

	@Override
	public void run() {
		SpoutChunk chunk;
		int x, y, z, x2, y2, z2;
		byte[] columns;
		long[] updates;
		TLongHashSet ignoreBlocks = new TLongHashSet();

		//used by the sky lighting bit
		int light = 0xF;
		boolean foundEnd = false;
		TLongIterator iter;
		while (this.running) {
			// == Initialize lighting in chunks where needed
			while (this.running) {
				synchronized (dirtyChunks) {
					chunk = dirtyChunks.poll();
					if (chunk == null || !chunk.isLoaded()) {
						break;
					}
				}

				if (chunk.requiresLightingInit.getAndSet(false)) {
					//initialize all columns
					columns = COLUMNS_ALL;
					synchronized (chunk.skyLightQueue) {
						chunk.skyLightQueue.clear();
					}

					//Initialize block lighting
					for (x = 0; x < 16; x++) {
						for (y = 0; y < 16; y++) {
							for (z = 0; z < 16; z++) {
								chunk.setBlockLight(x, y, z, chunk.getBlockMaterial(x, y, z).getLightLevel(), this.world);
							}
						}
					}
				} else {
					synchronized (chunk.skyLightQueue) {
						if (chunk.skyLightQueue.isEmpty()) {
							continue;
						}
						columns = chunk.skyLightQueue.toArray();
						chunk.skyLightQueue.clear();
					}
				}
				for (byte column : columns) {
					x = TNibbleDualHashSet.key1(column);
					z = TNibbleDualHashSet.key2(column);
					x2 = x + chunk.getBlockX();
					z2 = z + chunk.getBlockZ();
					//initialize this column of light
					SpoutChunk above = this.world.getChunk(chunk.getX(), chunk.getY() + 1, chunk.getZ(), false);
					light = (above == null || !above.isLoaded()) ? 15 : above.getBlockSkyLight(x, 0, z);
					
					foundEnd = light < 15;
					for (y = Chunk.CHUNK_SIZE - 1; y >= 0; --y) {
						//Don't check the opacity unless there is some light
						if (light > 0) {
							BlockMaterial type = chunk.getBlockMaterial(x, y, z);
							if (foundEnd || type.isSkyLightObstacle()) {
								foundEnd = true;
								light -= type.getOpacity();
								if (light < 0) {
									light = 0;
								}
							}
						}
						if (chunk.setBlockSkyLight(x, y, z, (byte) light, world)) {
							if (chunk.lightingDirtyCount.incrementAndGet() == SpoutChunk.LIGHTING_THRESHOLD) {
								chunk.setLightDirty(true);
							}
							y2 = y + chunk.getBlockY();
							if (light == 15) {
								ignoreBlocks.add(TInt21TripleHashSet.key(x2, y2, z2));
							} else {
								this.reportDirty(x2, y2, z2);
							}
							this.reportDirty(x2 + 1, y2, z2);
							this.reportDirty(x2 - 1, y2, z2);
							this.reportDirty(x2, y2 + 1, z2);
							this.reportDirty(x2, y2 - 1, z2);
							this.reportDirty(x2, y2, z2 + 1);
							this.reportDirty(x2, y2, z2 - 1);
						}
					}
					//schedule the chunk below
					SpoutChunk below = this.world.getChunk(chunk.getX(), chunk.getY() - 1, chunk.getZ(), false);
					if (below != null) {
						below.queueSkyLightUpdate(x, z);
					}
				}
				//cancel out block updates for 15-skylight blocks
				synchronized (this.dirtyBlocks) {
					iter = this.dirtyBlocks.iterator();
					while (iter.hasNext()) {
						if (ignoreBlocks.contains(iter.next())) {
							iter.remove();
						}
					}
				}
				ignoreBlocks.clear();
			}

			//============
			//Smooth sky block lighting till they are all gone
			while (this.running) {
				synchronized (this.dirtyBlocks) {
					if (this.dirtyBlocks.isEmpty()) {
						break;
					} else {
						updates = this.dirtyBlocks.toArray();
						this.dirtyBlocks.clear();
					}
				}
				for (long update : updates) {
					x = TInt21TripleHashSet.key1(update);
					y = TInt21TripleHashSet.key2(update);
					z = TInt21TripleHashSet.key3(update);
					chunk = this.world.getChunkFromBlock(x, y, z);
					if (chunk != null) {
						//calculate the sky light this block coordinate receives
						light = chunk.getBlockSkyLight(x, y, z);
						int oldlight = light;
						if (light < 15) {
							//calculate expected light using surrounding sources
							BlockMaterial mat = chunk.getBlockMaterial(x, y, z);
							light = mat.getOpacity();
							for (BlockFace face : BlockFaces.NESWBT) {
								x2 = x + (int) face.getOffset().getX();
								y2 = y + (int) face.getOffset().getY();
								z2 = z + (int) face.getOffset().getZ();
								light = Math.max(light, this.world.getBlockSkyLight(x2, y2, z2));
							}
							light -= mat.getOpacity();
							if (light != oldlight) {
								if (chunk.lightingDirtyCount.incrementAndGet() == SpoutChunk.LIGHTING_THRESHOLD) {
									chunk.setLightDirty(true);
								}
								chunk.setBlockSkyLight(x, y, z, (byte) light, this.world);
								this.reportDirty(x + 1, y, z);
								this.reportDirty(x - 1, y, z);
								this.reportDirty(x, y + 1, z);
								this.reportDirty(x, y - 1, z);
								this.reportDirty(x, y, z + 1);
								this.reportDirty(x, y, z - 1);
							}
						}
					}
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException ex) {}
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {}
		}
	}

	/*
	for (int i = 0; i < LIGHT_PER_TICK; i++) {
		synchronized (queuedLightingUpdates) {
			if (queuedLightingUpdates.isEmpty()) {
				break;
			}
			updates = queuedLightingUpdates.toArray();
			queuedLightingUpdates.clear();
		}
		//perform lighting updates
		for (int key : updates) {
			x = TByteTripleHashSet.key1(key);
			y = TByteTripleHashSet.key2(key);
			z = TByteTripleHashSet.key3(key);
			chunk = chunks[x >> Chunk.CHUNK_SIZE_BITS][y >> Chunk.CHUNK_SIZE_BITS][z >> Chunk.CHUNK_SIZE_BITS].get();
			if (chunk != null) {
				block = chunk.getBlock(x, y, z);
				block.setSkyLight(block.getReceivingSkyLight());
				//block.setLight(block.getReceivingLight());
			}
		}
	}
	*/
	
	
	//Some functions previously in SpoutBlock, will be converted so LEAVE IT ALONE!
//	@Override
//	public byte getReceivingLight() {
//		BlockMaterial main = this.getSubMaterial();
//		int level = main.getLightLevel() + main.getOpacity();
//		int x, y, z;
//		for (BlockFace face : BlockFaces.NESWBT) {
//			if (!main.occludes(face)) {
//				x = this.getX() + (int) face.getOffset().getX();
//				y = this.getY() + (int) face.getOffset().getY();
//				z = this.getZ() + (int) face.getOffset().getZ();
//				Chunk chunk = this.getWorld().getChunkFromBlock(x, y, z, false);
//				if (chunk != null) {
//					level = Math.max(level, this.translate(face).getLightTo(face.getOpposite()));
//				}
//			}
//		}
//		level -= main.getOpacity();
//		return (byte) level;
//	}
//
//	@Override
//	public byte getReceivingSkyLight() {
//		int level = this.getSkyLight();
//		if (level == 0xF) {
//			return 0xF; //this block is a sky light source
//		} else {
//			BlockMaterial main = this.getSubMaterial();
//			level = main.getOpacity();
//			int x, y, z;
//			for (BlockFace face : BlockFaces.NESWBT) {
//				if (!main.occludes(face)) {
//					x = this.getX() + (int) face.getOffset().getX();
//					y = this.getY() + (int) face.getOffset().getY();
//					z = this.getZ() + (int) face.getOffset().getZ();
//					Chunk chunk = this.getWorld().getChunkFromBlock(x, y, z, false);
//					if (chunk != null) {
//						level = Math.max(level, this.translate(face).getSkyLightTo(face.getOpposite()));
//					}
//				}
//			}
//			level -= main.getOpacity();
//			return (byte) level;
//		}
//	}

}
