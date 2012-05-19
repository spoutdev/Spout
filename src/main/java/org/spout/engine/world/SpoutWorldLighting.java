package org.spout.engine.world;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.HashUtil;
import org.spout.api.util.set.TInt21TripleHashSet;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncManager;

public class SpoutWorldLighting extends AsyncManager {

	private final SpoutWorld world;
	
	/**
	 * The blocks this lighting manager still has to operate on
	 */
	private final TInt21TripleHashSet dirtyBlocks = new TInt21TripleHashSet();

	/**
	 * The chunks this lighting manager still has to perform lighting updates on<br>
	 * It excludes all the block updates scheduled under 'dirtyBlocks'.
	 */
	private final TInt21TripleHashSet dirtyChunks = new TInt21TripleHashSet();

	public SpoutWorldLighting(SpoutWorld world, int maxStage, AsyncExecutor executor) {
		super(maxStage, executor);
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
			//dirtyBlocks.add(x, y, z);
		}
	}

	/**
	 * Marks a sky column dirty
	 * 
	 * @param x coordinate of the block column
	 * @param z coordinate of the block column
	 */
	public void reportChunkDirty(int x, int y, int z) {
		synchronized (dirtyChunks) {
			//dirtyChunks.add(x, y, z);
		}
	}

	/**
	 * Processes the queued lighting updates for this chunk.
	 * <p/>
	 * This should only be called from the SpoutRegion that manages this chunk, during the first tick stage.
	 */
	protected void processQueuedLighting(SpoutChunk chunk) {
		if (chunk.requiresLightingInit.getAndSet(false)) {
			int x, y, z;
			BlockMaterial mat;
			for (x = 0; x < Chunk.CHUNK_SIZE; x++) {
				for (z = 0; z < Chunk.CHUNK_SIZE; z++) {
					recalculateSkyLighting(chunk, x, z);
					for (y = 0; y < Chunk.CHUNK_SIZE; y++) {
						mat = chunk.getBlockMaterial(x, y, z).getSubMaterial(chunk.getBlockData(x, y, z));
						chunk.setBlockLight(x, y, z, mat.getLightLevel(), this.world);
					}
				}
			}
		} else {
			byte[] queue;
			synchronized (chunk.skyLightQueue) {
				if (chunk.skyLightQueue.isEmpty()) {
					return;
				}
				queue = chunk.skyLightQueue.toArray();
				chunk.skyLightQueue.clear();
			}
			int x, z;
			for (byte b : queue) {
				x = HashUtil.byteToNibble1(b);
				z = HashUtil.byteToNibble2(b);
				recalculateSkyLighting(chunk, x, z);
			}
		}
	}

	/**
	 * Recalculates the sky light in the x, z column.
	 * <p/>
	 * May queue more lighting updates in chunks underneath.
	 * @param x coordinate
	 * @param z coordinate
	 */
	private void recalculateSkyLighting(SpoutChunk chunk, int x, int z) {
		byte prevValue = 0xF;
		boolean foundEnd = false;
		for (int y = Chunk.CHUNK_SIZE - 1; y >= 0; --y) {
			//Don't check the opacity unless there is some light
			if (prevValue > 0) {
				BlockMaterial type = chunk.getBlockMaterial(x, y, z);
				if (!foundEnd && type.isHeightLimiter()) {
					foundEnd = true;
				}
				if (foundEnd) {
					prevValue -= type.getOpacity();
					if (prevValue < 0) {
						prevValue = 0;
					}
				}
			}
			chunk.setBlockSkyLight(x, y, z, prevValue, world);
		}
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {

	}
	
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
	
	
	@Override
	public void finalizeRun() throws InterruptedException {
		
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		
	}

	@Override
	public void haltRun() throws InterruptedException {
		
	}
}
