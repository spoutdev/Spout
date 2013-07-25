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
package org.spout.engine.world.dynamic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.Material;
import org.spout.api.material.range.EffectRange;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

/**
 * This class contains the dynamic block updates.  There are 3 data structures that are kept in sync.<br> <br> blockToUpdateMap - This maps the packed (x, y, z) block coords to the DynamicBlockUpdates
 * for that block (stored as a linked list) chunkToUpdateMap - this maps the packed (x, y, z) chunk coords to the DynamicBlockUpdates for that chunk (stored as a hashset) queuedUpdates - the actual
 * queue of dynamic updates, stored in a TreeMap
 */
public class DynamicBlockUpdateTree {
	private final SpoutRegion region;
	private final SpoutWorld world;
	private final TreeSet<DynamicBlockUpdate> queuedUpdates = new TreeSet<DynamicBlockUpdate>();
	private final TIntObjectHashMap<DynamicBlockUpdate> blockToUpdateMap = new TIntObjectHashMap<DynamicBlockUpdate>();
	private final TIntObjectHashMap<HashSet<DynamicBlockUpdate>> chunkToUpdateMap = new TIntObjectHashMap<HashSet<DynamicBlockUpdate>>();
	/**
	 * Keeps a queue of a lists of DynamicBlockUpdates. Lists are only added when previously saved chunks are loaded, and added to the queue
	 */
	private final ConcurrentLinkedQueue<List<DynamicBlockUpdate>> pendingLists = new ConcurrentLinkedQueue<List<DynamicBlockUpdate>>();
	/**
	 * A queue of the reset block update requests (which triggers the firstUpdate of DynamicBlockMaterials)
	 */
	private final ConcurrentLinkedQueue<Point> resetPending = new ConcurrentLinkedQueue<Point>();
	/**
	 * A map of reset block update requests, which prevents duplicate reset requests at a single x, y, z location
	 */
	private final ConcurrentHashMap<Point, Boolean> resetPendingMap = new ConcurrentHashMap<Point, Boolean>();
	private Thread regionThread;
	@SuppressWarnings ("unused")
	private final Thread mainThread;
	private final static int localStages = TickStage.DYNAMIC_BLOCKS | TickStage.PHYSICS;
	private final static int globalStages = TickStage.GLOBAL_DYNAMIC_BLOCKS | TickStage.GLOBAL_PHYSICS;
	private int lastUpdates;

	protected DynamicBlockUpdateTree(Thread thread, SpoutRegion region) {
		this.region = region;
		this.mainThread = thread;
		this.world = region.getWorld();
	}

	public DynamicBlockUpdateTree(SpoutRegion region) {
		this(((SpoutScheduler) Spout.getScheduler()).getMainThread(), region);
	}

	public void setRegionThread(Thread t) {
		this.regionThread = t;
	}

	public void resetBlockUpdates(Chunk c) {
		int x = c.getBlockX() & Region.BLOCKS.MASK;
		int y = c.getBlockY() & Region.BLOCKS.MASK;
		int z = c.getBlockZ() & Region.BLOCKS.MASK;
		Point p = new ChunkPoint(this.world, x, y, z);
		boolean success = false;
		while (!success) {
			Boolean b = resetPendingMap.putIfAbsent(p, Boolean.FALSE);
			if (b == null) {
				success = true;
			} else if (b.equals(Boolean.FALSE)) { // Chunk set already sent
				return;
			} else {
				success = resetPendingMap.replace(p, b, Boolean.FALSE);
				if (!resetPending.remove(p)) {
					throw new IllegalStateException("Unable to remove old Point from reset pending queue when replacing with ChunkPoint");
				}
			}
		}
		resetPending.add(p);
	}

	public void resetBlockUpdates(int x, int y, int z) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		Point p = new Point(this.world, x, y, z);
		if (resetPendingMap.putIfAbsent(p, Boolean.TRUE) == null) {
			resetPending.add(p);
		}
	}

	public void syncResetBlockUpdates(int x, int y, int z) {
		checkStages();
		syncResetBlockUpdates(x, y, z, world.getAge(), false);
	}

	private void syncResetBlockUpdates(int x, int y, int z, long currentTime, boolean triggerPlacement) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;

		removeAll(DynamicBlockUpdate.getBlockPacked(x, y, z));

		if (!triggerPlacement) {
			return;
		}

		Chunk c = region.getChunkFromBlock(x, y, z, LoadOption.NO_LOAD);
		if (c == null) {
			return;
		}

		Material m = c.getBlockMaterial(x, y, z);

		if (m instanceof DynamicMaterial) {
			Block b = c.getBlock(x, y, z);
			try {
				((DynamicMaterial) m).onFirstUpdate(b, currentTime);
			} catch (RuntimeException e) {
				Spout.getLogger().severe("Unable to execute dynamic update for " + m.getClass().getSimpleName());
				throw e;
			}
		}
	}

	/**
	 * Queues an immediate block update for the x, y, z location
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @param exclusive resets any queued block updates for the location and triggers a first update for the block
	 * @return the dynamic update entry for the location
	 */
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, boolean exclusive) {
		return queueBlockUpdates(x, y, z, 0L, exclusive);
	}

	/**
	 * Queues a block update for the x, y, z location at the given updateTime
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @param updateTime the time at which the update should occur, in terms of worldAge
	 * @param exclusive resets any queued block updates for the location and triggers a first update for the block
	 * @return the dynamic update entry for the location
	 */
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, long updateTime, boolean exclusive) {
		return queueBlockUpdates(x, y, z, updateTime, 0, exclusive);
	}

	/**
	 * Queues a block update for the x, y, z location at the given updateTime
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @param updateTime the time at which the update should occur, in terms of worldAge
	 * @param data for the update, optional.
	 * @param exclusive resets any queued block updates for the location and triggers a first update for the block
	 * @return the dynamic update entry for the location
	 */
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, long updateTime, int data, boolean exclusive) {
		checkStages();
		if (exclusive) {
			syncResetBlockUpdates(x, y, z, 0, false);
		}
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		return add(new DynamicBlockUpdate(x, y, z, updateTime, data));
	}

	private void checkStages() {
		// Note: This is a weaker check that before
		//       Access is open during the global update stages, but access should be 
		//       restricted to neighbor in the sequence
		TickStage.checkStage(globalStages, localStages, regionThread);
	}

	public void addDynamicBlockUpdates(List<DynamicBlockUpdate> list) {
		if (list.size() > 0) {
			pendingLists.add(list);
		}
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
		TickStage.checkStage(TickStage.SNAPSHOT, regionThread);
		Set<DynamicBlockUpdate> toRemove = getDynamicBlockUpdates(c);
		if (toRemove == null) {
			return true;
		}

		if (toRemove.size() <= 0) {
			return true;
		}

		List<DynamicBlockUpdate> list = new ArrayList<DynamicBlockUpdate>(toRemove);
		for (DynamicBlockUpdate dm : list) {
			if (remove(dm) == null) {
				throw new IllegalStateException("Expected update not present when removing all updates for chunk " + c);
			}
		}
		return false;
	}

	public void commitAsyncPending(long currentTime) {
		TickStage.checkStage(TickStage.DYNAMIC_BLOCKS, regionThread);
		List<DynamicBlockUpdate> l;
		while ((l = pendingLists.poll()) != null) {
			for (DynamicBlockUpdate update : l) {
				add(update);
			}
		}

		Point p;
		while ((p = resetPending.poll()) != null) {
			if (resetPendingMap.remove(p) == null) {
				throw new IllegalStateException("Dynamic block reset pending map and queue mismatch");
			}
			int bx = p.getBlockX();
			int by = p.getBlockY();
			int bz = p.getBlockZ();

			if (p instanceof ChunkPoint) {
				for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
					for (int y = 0; y < Chunk.BLOCKS.SIZE; y++) {
						for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
							syncResetBlockUpdates(bx + x, by + y, bz + z, currentTime, true);
						}
					}
				}
			} else {
				syncResetBlockUpdates(bx, by, bz, currentTime, true);
			}
		}
	}

	public int getLastUpdates() {
		return lastUpdates;
	}

	public void resetLastUpdates() {
		lastUpdates = 0;
	}

	/**
	 * Updates all region dynamic updates, and returns a list of all multi-region updates that could not be executed
	 *
	 * @param currentTime the current world time
	 * @param thresholdTime the threshold time for updates (updates before this time should be executed)
	 * @return list of multi-region updates that could not be executed
	 */
	public List<DynamicBlockUpdate> updateDynamicBlocks(long currentTime, long thresholdTime) {
		DynamicBlockUpdate first;

		ArrayList<DynamicBlockUpdate> multiRegionUpdates = null;

		while ((first = getNextUpdate(thresholdTime)) != null) {
			if (!updateDynamicBlock(currentTime, first, false).isLocal()) {
				if (multiRegionUpdates == null) {
					multiRegionUpdates = new ArrayList<DynamicBlockUpdate>();
				}
				multiRegionUpdates.add(first);
			}
		}
		return multiRegionUpdates;
	}

	/**
	 * Executes a dynamic block update. The update may not execute if not forced, and it spans multiple regions
	 *
	 * @param currentTime the current world time
	 * @param update the dynamic block update to execute
	 * @param force this update to execute even when the update spans multiple regions
	 * @return the result of the update
	 */
	public UpdateResult updateDynamicBlock(long currentTime, DynamicBlockUpdate update, boolean force) {
		checkStages();
		int bx = update.getX();
		int by = update.getY();
		int bz = update.getZ();

		SpoutChunk c = region.getChunkFromBlock(bx, by, bz, LoadOption.NO_LOAD);

		if (c == null) {
			return UpdateResult.NOT_LOADED;
		}

		if (!c.isPopulated()) {
			return UpdateResult.NOT_POPULATED;
		}

		Material m = c.getBlockMaterial(bx, by, bz);

		if (!(m instanceof DynamicMaterial)) {
			return UpdateResult.NOT_DYNAMIC;
		}

		DynamicMaterial dm = (DynamicMaterial) m;
		EffectRange range = dm.getDynamicRange();
		if (!force && !range.isRegionLocal(bx, by, bz)) {
			return UpdateResult.NON_LOCAL;
		} else {
			c.setModified();
			Block b = c.getBlock(bx, by, bz);
			try {
				dm.onDynamicUpdate(b, update.getNextUpdate(), update.getData());
			} catch (RuntimeException e) {
				Spout.getLogger().severe("Unable to execute dynamic update for " + dm.getClass().getSimpleName());
				throw e;
			}
			lastUpdates++;
			return UpdateResult.DONE;
		}
	}

	/**
	 * The next dynamic block update time in the queue
	 *
	 * @return update time
	 */
	public long getFirstDynamicUpdateTime() {
		checkStages();
		if (queuedUpdates.isEmpty()) {
			return SpoutScheduler.END_OF_THE_WORLD;
		}
		return queuedUpdates.first().getNextUpdate();
	}

	/**
	 * Gets the next dynamic block update valid for the threshold time, if any exist
	 *
	 * @param thresholdTime in terms of world age
	 * @return next dynamic block update, or null if no valid updates exist
	 */
	public DynamicBlockUpdate getNextUpdate(long thresholdTime) {
		checkStages();
		if (queuedUpdates.isEmpty()) {
			return null;
		}

		DynamicBlockUpdate first = queuedUpdates.first();
		if (first == null) {
			return null;
		}

		if (first.getNextUpdate() > thresholdTime) {
			return null;
		}

		if (remove(first) != first) {
			throw new IllegalStateException("queued updates for dynamic block updates violated threading rules");
		}

		return first;
	}

	/**
	 * Adds a dynamic block update to the queue
	 *
	 * @param update the update to add
	 * @return the previous update
	 */
	private DynamicBlockUpdate add(final DynamicBlockUpdate update) {
		int key = update.getPacked();
		DynamicBlockUpdate oldRoot = blockToUpdateMap.get(key);
		DynamicBlockUpdate previous = null;
		if (oldRoot != null) {
			DynamicBlockUpdate current = oldRoot;
			while (current != null) {
				if (current.getNextUpdate() == update.getNextUpdate()) {
					if (remove(current) != current) {
						throw new IllegalStateException("Previous update disappeared when adding a new update");
					}
					previous = current;
					oldRoot = blockToUpdateMap.get(key);
					break;
				} else {
					// Obtain next update of this block
					current = current.getNext();
				}
			}
		}

		if (oldRoot != null) {
			DynamicBlockUpdate newRoot = oldRoot.add(update);
			if (newRoot != oldRoot) {
				blockToUpdateMap.put(key, newRoot);
			}
		} else {
			blockToUpdateMap.put(key, update);
		}

		queuedUpdates.add(update);
		HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(update.getChunkPacked());
		if (chunkSet == null) {
			chunkSet = new HashSet<DynamicBlockUpdate>();
			chunkToUpdateMap.put(update.getChunkPacked(), chunkSet);
		}
		chunkSet.add(update);

		return previous;
	}

	/**
	 * Removes a specific update
	 *
	 * @param update the update to remove
	 * @return the update, if removed
	 */
	private DynamicBlockUpdate remove(DynamicBlockUpdate update) {
		boolean removed = false;
		int packedKey = update.getPacked();
		DynamicBlockUpdate root = blockToUpdateMap.get(packedKey);
		DynamicBlockUpdate current = root;
		DynamicBlockUpdate previous = null;
		boolean rootChanged = false;
		while (current != null) {
			if (current != update) {
				previous = current;
				current = current.getNext();
				continue;
			}
			if (removed) {
				throw new IllegalStateException("Dynamic update appeared twice in the linked list");
			}
			removed = true;
			if (!queuedUpdates.remove(current)) {
				throw new IllegalStateException("Dynamic block update missing from queue when removed");
			}
			int currentPacked = current.getChunkPacked();
			HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(currentPacked);
			if (chunkSet == null || !chunkSet.remove(current)) {
				throw new IllegalStateException("Dynamic block update missing from chunk when removed");
			}

			if (chunkSet.isEmpty()) {
				if (chunkToUpdateMap.remove(currentPacked) == null) {
					throw new IllegalStateException("Removing updates for dynamic block updates violated threading rules");
				}
			}
			if (current == root) {
				root = current.getNext();
				current = root;
				rootChanged = true;
			} else {
				if (previous.remove(current) != previous) {
					throw new IllegalStateException("Removing current from previous should not move root");
				}
				current = previous.getNext();
			}
		}
		if (rootChanged) {
			if (root != null) {
				blockToUpdateMap.put(packedKey, root);
			} else {
				blockToUpdateMap.remove(packedKey);
			}
		}
		return removed ? update : null;
	}

	/**
	 * Removes all updates at the given block location
	 *
	 * @param update the packed value for the block location
	 * @return the old updates as a linked list
	 */
	private DynamicBlockUpdate removeAll(int packed) {
		DynamicBlockUpdate oldRoot = blockToUpdateMap.remove(packed);
		if (oldRoot == null) {
			return null;
		}

		DynamicBlockUpdate current = oldRoot;
		while (current != null) {
			if (!queuedUpdates.remove(current)) {
				throw new IllegalStateException("Dynamic block update missing from queue when removed");
			}
			int previousPacked = current.getChunkPacked();
			HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(previousPacked);
			if (chunkSet == null || !chunkSet.remove(current)) {
				throw new IllegalStateException("Dynamic block update missing from chunk when removed");
			}

			if (chunkSet.isEmpty()) {
				if (chunkToUpdateMap.remove(previousPacked) == null) {
					throw new IllegalStateException("Removing updates for dynamic block updates violated threading rules");
				}
			}
			current = current.getNext();
		}
		return oldRoot;
	}

	public static enum UpdateResult {
		NON_LOCAL,
		DONE,
		NOT_DYNAMIC,
		NOT_LOADED,
		NOT_POPULATED;

		public boolean isLocal() {
			return this != NON_LOCAL;
		}

		public boolean isUpdated() {
			return this == DONE;
		}
	}
}
