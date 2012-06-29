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
package org.spout.engine.world.dynamic;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.Spout;
import org.spout.api.exception.IllegalTickSequenceException;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.Material;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

/**
 * This class contains the dynamic block updates.  There are 3 data structures that are kept in sync.<br>
 * <br>
 * blockToUpdateMap - This maps the packed (x, y, z) block coords to the DynamicBlockUpdates for that block (stored as a linked list)
 * chunkToUpdateMap - this maps the packed (x, y, z) chunk coords to the DynamicBlockUpdates for that chunk (stored as a hashset)
 * queuedUpdates - the actual queue of dynamic updates, stored in a TreeMap
 */
public class DynamicBlockUpdateTree {
	
	private final SpoutRegion region;
	private final SpoutWorld world;
	
	private TreeSet<DynamicBlockUpdate> queuedUpdates = new TreeSet<DynamicBlockUpdate>();
	private TIntObjectHashMap<DynamicBlockUpdate> blockToUpdateMap = new TIntObjectHashMap<DynamicBlockUpdate>();
	private TIntObjectHashMap<HashSet<DynamicBlockUpdate>> chunkToUpdateMap = new TIntObjectHashMap<HashSet<DynamicBlockUpdate>>();
	private ConcurrentLinkedQueue<PointAlone> resetPending = new ConcurrentLinkedQueue<PointAlone>();
	private ConcurrentHashMap<PointAlone, Boolean> resetPendingMap = new ConcurrentHashMap<PointAlone, Boolean>();
	private ConcurrentLinkedQueue<List<DynamicBlockUpdate>> pendingLists = new ConcurrentLinkedQueue<List<DynamicBlockUpdate>>();
	private TIntHashSet processed = new TIntHashSet();
	@SuppressWarnings("unused")
        private final static Vector3[] zeroVector3Array = new Vector3[] {Vector3.ZERO};
	private final Thread regionThread;
	private final Thread mainThread;
	private final static int localStages = TickStage.DYNAMIC_BLOCKS | TickStage.PHYSICS;
	private final static int globalStages = TickStage.GLOBAL_DYNAMIC_BLOCKS | TickStage.GLOBAL_PHYSICS;
	private final static List<DynamicBlockUpdate> emptyList = new ArrayList<DynamicBlockUpdate>(0);
	private int lastUpdates;
	
	public DynamicBlockUpdateTree(SpoutRegion region) {
		this.region = region;
		this.regionThread = region.getExceutionThread();
		this.mainThread = ((SpoutScheduler)Spout.getScheduler()).getMainThread();
		this.world = region.getWorld();
	}

	public void resetBlockUpdates(int x, int y, int z) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		PointAlone p = new PointAlone(this.world, x, y, z);
		if (resetPendingMap.putIfAbsent(p, Boolean.TRUE) == null) {
			resetPending.add(p);
		}
	}
	
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z) {
		checkStages();
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		return add(new DynamicBlockUpdate(x, y, z, 0, region.getWorld().getAge(), 0, null));
	}
	
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, long updateTime) {
		checkStages();
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		return add(new DynamicBlockUpdate(x, y, z, updateTime, region.getWorld().getAge(), 0, null));
	}
	
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, long updateTime, Object hint) {
		checkStages();
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		return add(new DynamicBlockUpdate(x, y, z, updateTime, region.getWorld().getAge(), 0, hint));
	}
	
	public DynamicUpdateEntry queueBlockUpdates(int x, int y, int z, long updateTime, int data, Object hint) {
		checkStages();
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		return add(new DynamicBlockUpdate(x, y, z, updateTime, region.getWorld().getAge(), data, hint));
	}
	
	private void checkStages() {
		if (Thread.currentThread() == this.regionThread) {
			TickStage.checkStage(localStages);
		} else if (Thread.currentThread() == mainThread){
			TickStage.checkStage(globalStages);
		} else {
			throw new IllegalTickSequenceException(TickStage.ALL_PHYSICS_AND_DYNAMIC, TickStage.getStageInt());
		}
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
		processed.clear();
		PointAlone p;
		while ((p = resetPending.poll()) != null) {
			if (!resetPendingMap.remove(p)) {
				throw new IllegalStateException("Dynamic block reset pending map and queue mismatch");
			}
			int packed = DynamicBlockUpdate.getPointPacked(p);
			if (!processed.add(packed)) {
				continue;
			}
			int bx = p.getBlockX() & Region.BLOCKS.MASK;
			int by = p.getBlockY() & Region.BLOCKS.MASK;
			int bz = p.getBlockZ() & Region.BLOCKS.MASK;
			
			removeAll(DynamicBlockUpdate.getBlockPacked(bx, by, bz));

			Chunk c = region.getChunkFromBlock(bx, by, bz, LoadOption.NO_LOAD);
			if (c == null) {
				continue;
			}

			Block b =  c.getBlock(bx, by, bz);
			Material m = b.getMaterial();
			
			if (m instanceof DynamicMaterial) {
				DynamicMaterial dm = (DynamicMaterial)m;
				dm.onPlacement(b, region, currentTime);
			}
		}
		processed.clear();
	}
	
	public int getLastUpdates() {
		return lastUpdates;
	}
	
	public void resetLastUpdates() {
		lastUpdates = 0;
	}
	
	public List<DynamicBlockUpdate> updateDynamicBlocks(long currentTime, long thresholdTime) {
		DynamicBlockUpdate first;
		
		ArrayList<DynamicBlockUpdate> multiRegionUpdates = null;
		
		while ((first = getNextUpdate(thresholdTime)) != null) {
			if (!updateDynamicBlock(currentTime, first, false)) {
				if (multiRegionUpdates == null) {
					multiRegionUpdates = new ArrayList<DynamicBlockUpdate>();
				}
				multiRegionUpdates.add(first);
			}
		}
		if (thresholdTime < currentTime && multiRegionUpdates == null) {
			return emptyList;
		} else {
			return multiRegionUpdates;
		}
	}
	
	public boolean updateDynamicBlock(long currentTime, DynamicBlockUpdate update, boolean force) {
		checkStages();
		int bx = update.getX();
		int by = update.getY();
		int bz = update.getZ();

		Chunk c = region.getChunkFromBlock(bx, by, bz, LoadOption.NO_LOAD);

		if (c == null) {
			// TODO - this shouldn't happen - maybe a warning
			return true;
		}
		
		Block b =  c.getBlock(bx, by, bz);
		Material m = b.getMaterial();
		
		if (!(m instanceof DynamicMaterial)) {
			return true;
		}

		DynamicMaterial dm = (DynamicMaterial)m;
		EffectRange range = dm.getDynamicRange();
		if (!force && !range.isRegionLocal(bx, by, bz)) {
			return false;
		} else {
			dm.onDynamicUpdate(b, region, update.getNextUpdate(), update.getQueuedTime(), update.getData(), update.getHint());
			lastUpdates++;
			return true;
		}
	}
	
	public long getFirstDynamicUpdateTime() {
		checkStages();
		if (queuedUpdates.isEmpty()) {
			return SpoutScheduler.END_OF_THE_WORLD;
		}
		return queuedUpdates.first().getNextUpdate();
	}
	
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
	 * Adds an update
	 * 
	 * @param update the update to add
	 * @return the previous update
	 */
	private DynamicBlockUpdate add(DynamicBlockUpdate update) {
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
	
			if (chunkSet.size() == 0) {
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

			if (chunkSet.size() == 0) {
				if (chunkToUpdateMap.remove(previousPacked) == null) {
					throw new IllegalStateException("Removing updates for dynamic block updates violated threading rules");
				}
			}
			current = current.getNext();
		}
		return oldRoot;
	}
}
