package org.spout.engine.world.dynamic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.Material;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.hashing.ByteTripleHashed;

import org.spout.engine.world.SpoutRegion;

public class DynamicBlockUpdateTree {
	private final SpoutRegion region;
	private TreeSet<DynamicBlockUpdate> queuedUpdates = new TreeSet<DynamicBlockUpdate>();
	private TIntObjectHashMap<DynamicBlockUpdate> blockToUpdateMap = new TIntObjectHashMap<DynamicBlockUpdate>();
	private TIntObjectHashMap<HashSet<DynamicBlockUpdate>> chunkToUpdateMap = new TIntObjectHashMap<HashSet<DynamicBlockUpdate>>();
	private ConcurrentLinkedQueue<Point> pending = new ConcurrentLinkedQueue<Point>();
	private ConcurrentLinkedQueue<List<DynamicBlockUpdate>> pendingLists = new ConcurrentLinkedQueue<List<DynamicBlockUpdate>>();
	private TIntHashSet processed = new TIntHashSet();
	private final static int regionMask = (Chunk.CHUNK_SIZE * Region.REGION_SIZE) - 1;
	private final static Vector3[] zeroVector3Array = new Vector3[]{Vector3.ZERO};

	public DynamicBlockUpdateTree(SpoutRegion region) {
		this.region = region;
	}

	public void resetBlockUpdates(int x, int y, int z) {
		x &= regionMask;
		y &= regionMask;
		z &= regionMask;
		resetBlockUpdatesRaw(new Point(region.getWorld(), x, y, z));
	}

	private void resetBlockUpdatesRaw(Point p) {
		pending.add(p);
	}

	public void addDynamicBlockUpdates(List<DynamicBlockUpdate> list) {
		pendingLists.add(list);
	}

	/**
	 * NOTE: Do NOT modify the returned set
	 */
	public Set<DynamicBlockUpdate> getDynamicBlockUpdates(Chunk c) {
		TickStage.checkStage(TickStage.SNAPSHOT);
		int packed = DynamicBlockUpdate.getChunkPacked(c);
		return chunkToUpdateMap.get(packed);
	}

	public boolean removeDynamicBlockUpdates(Chunk c) {
		TickStage.checkStage(TickStage.SNAPSHOT);
		Set<DynamicBlockUpdate> toRemove = getDynamicBlockUpdates(c);
		if (toRemove != null && toRemove.size() > 0) {
			List<DynamicBlockUpdate> list = new ArrayList<DynamicBlockUpdate>(toRemove);
			for (DynamicBlockUpdate dm : list) {
				remove(dm);
			}
			return false;
		} else {
			return true;
		}
	}

	public void commitPending(long currentTime) {
		TickStage.checkStage(TickStage.FINALIZE);
		List<DynamicBlockUpdate> l;
		while ((l = pendingLists.poll()) != null) {
			for (DynamicBlockUpdate update : l) {
				add(update);
			}
		}
		Point p;
		while ((p = pending.poll()) != null) {
			int packed = ByteTripleHashed.key(p.getBlockX(), p.getBlockY(), p.getBlockZ());
			if (!processed.add(packed)) {
				continue;
			}
			int bx = p.getBlockX() & regionMask;
			int by = p.getBlockY() & regionMask;
			int bz = p.getBlockZ() & regionMask;

			int rShift = Region.REGION_SIZE_BITS;
			Chunk c = region.getChunk(bx >> rShift, by >> rShift, bz >> rShift, false);
			if (c == null) {
				continue;
			}

			Block b = c.getBlock(bx, by, bz);
			Material m = b.getMaterial();

			if (m instanceof DynamicMaterial) {
				DynamicMaterial dm = (DynamicMaterial) m;
				long nextUpdate = dm.update(b, 0, 0, true);
				if (nextUpdate > 0) {
					add(new DynamicBlockUpdate(bx, by, bz, nextUpdate, currentTime));
				}
			}
		}
		processed.clear();
	}

	public List<DynamicBlockUpdate> updateDynamicBlocks(long currentTime) {
		DynamicBlockUpdate first;

		ArrayList<DynamicBlockUpdate> multiRegionUpdates = null;

		while ((first = getNextUpdate(currentTime)) != null) {
			if (!updateDynamicBlock(currentTime, first, false)) {
				if (multiRegionUpdates == null) {
					multiRegionUpdates = new ArrayList<DynamicBlockUpdate>();
				}
				multiRegionUpdates.add(first);
			}
		}
		return multiRegionUpdates;
	}

	public boolean updateDynamicBlock(long currentTime, DynamicBlockUpdate update, boolean force) {
		int bx = update.getX();
		int by = update.getY();
		int bz = update.getZ();

		int rShift = Region.REGION_SIZE_BITS;
		Chunk c = region.getChunk(bx >> rShift, by >> rShift, bz >> rShift, false);

		if (c == null) {
			// TODO - this shouldn't happen - maybe a warning
			return true;
		}

		Block b = c.getBlock(bx, by, bz);
		Material m = b.getMaterial();

		if (m instanceof DynamicMaterial) {
			DynamicMaterial dm = (DynamicMaterial) m;
			Vector3[] range = (force) ? zeroVector3Array : dm.maxRange();
			if (range == null || range.length < 1) {
				range = zeroVector3Array;
			}
			Vector3 rangeHigh = range[0];
			Vector3 rangeLow = range.length < 2 ? range[0] : range[1];

			int rhx = (int) rangeHigh.getX();
			int rhy = (int) rangeHigh.getY();
			int rhz = (int) rangeHigh.getZ();
			if (rhx < 0 || rhy < 0 || rhz < 0) {
				throw new IllegalArgumentException("Max range values must be greater or equal to 0");
			}
			int rlx = (int) rangeLow.getX();
			int rly = (int) rangeLow.getY();
			int rlz = (int) rangeLow.getZ();
			if (rlx < 0 || rly < 0 || rlz < 0) {
				throw new IllegalArgumentException("Max range values must be greater or equal to 0");
			}
			int maxx = bx + rhx;
			int maxy = by + rhy;
			int maxz = bz + rhz;
			int minx = bx - rlx;
			int miny = by - rly;
			int minz = bz - rlz;
			int rs = Region.REGION_SIZE * Chunk.CHUNK_SIZE;
			if (maxx >= rs || maxy >= rs || maxz >= rs || minx < 0 || miny < 0 || minz < 0) {
				return false;
			} else {
				long nextUpdate = dm.update(b, update.getNextUpdate(), update.getLastUpdate(), false);
				if (nextUpdate > 0) {
					add(new DynamicBlockUpdate(bx, by, bz, nextUpdate, currentTime));
				}
				return true;
			}
		} else {
			return true;
		}
	}

	public DynamicBlockUpdate getNextUpdate(long currentTime) {
		if (queuedUpdates.isEmpty()) {
			return null;
		} else {
			DynamicBlockUpdate first = queuedUpdates.first();
			if (first == null) {
				return null;
			} else {
				if (first.getNextUpdate() <= currentTime) {
					if (!remove(first)) {
						throw new IllegalStateException("queued updates for dynamic block updates violated threading rules");
					} else {
						return first;
					}
				} else {
					return null;
				}
			}
		}
	}

	private boolean add(DynamicBlockUpdate update) {
		boolean alreadyPresent = remove(update);
		queuedUpdates.add(update);
		blockToUpdateMap.put(update.getPacked(), update);
		HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(update.getChunkPacked());
		if (chunkSet == null) {
			chunkSet = new HashSet<DynamicBlockUpdate>();
			chunkToUpdateMap.put(update.getChunkPacked(), chunkSet);
		}
		chunkSet.add(update);
		return alreadyPresent;
	}

	private boolean remove(DynamicBlockUpdate update) {
		int packed = update.getPacked();
		DynamicBlockUpdate previous = blockToUpdateMap.remove(packed);
		if (previous != null) {
			if (!queuedUpdates.remove(previous)) {
				throw new IllegalStateException("Dynamic block update missing from queue when removed");
			}
			int previousPacked = previous.getChunkPacked();
			HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(previousPacked);
			if (chunkSet == null || !chunkSet.remove(previous)) {
				throw new IllegalStateException("Dynamic block update missing from chunk when removed");
			} else {
				if (chunkSet.size() == 0) {
					if (chunkToUpdateMap.remove(previousPacked) == null) {
						throw new IllegalStateException("Removing updates for dynamic block updates violated threading rules");
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
