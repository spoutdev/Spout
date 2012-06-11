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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.InsertionPolicy;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.DynamicMaterial;
import org.spout.api.material.Material;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.world.SpoutRegion;

/**
 * This class contains the dynamic block updates.  There are 3 data structures that are kept in sync.<br>
 * <br>
 * blockToUpdateMap - This maps the packed (x, y, z) block coords to the DynamicBlockUpdates for that block (stored as a linked list)
 * chunkToUpdateMap - this maps the packed (x, y, z) chunk coords to the DynamicBlockUpdates for that chunk (stored as a hashset)
 * queuedUpdates - the actual queue of dynamic updates, stored in a TreeMap
 */
public class DynamicBlockUpdateTree {
	
	private final SpoutRegion region;
	
	private TreeSet<DynamicBlockUpdate> queuedUpdates = new TreeSet<DynamicBlockUpdate>();
	private TIntObjectHashMap<DynamicBlockUpdate> blockToUpdateMap = new TIntObjectHashMap<DynamicBlockUpdate>();
	private TIntObjectHashMap<HashSet<DynamicBlockUpdate>> chunkToUpdateMap = new TIntObjectHashMap<HashSet<DynamicBlockUpdate>>();
	private ConcurrentLinkedQueue<PointAlone> pending = new ConcurrentLinkedQueue<PointAlone>();
	private ConcurrentLinkedQueue<PointAlone> resetPending = new ConcurrentLinkedQueue<PointAlone>();
	private ConcurrentLinkedQueue<List<DynamicBlockUpdate>> pendingLists = new ConcurrentLinkedQueue<List<DynamicBlockUpdate>>();
	private TIntHashSet processed = new TIntHashSet();
	private final static Vector3[] zeroVector3Array = new Vector3[] {Vector3.ZERO};
	private final Thread regionThread;
	
	public DynamicBlockUpdateTree(SpoutRegion region) {
		this.region = region;
		this.regionThread = region.getExceutionThread();
	}

	public void resetBlockUpdates(int x, int y, int z) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		resetPending.add(new PointAlone(null, x, y, z));
	}
	
	public void queueBlockUpdates(int x, int y, int z) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		pending.add(new PointAlone(null, x, y, z));
	}
	
	public void queueBlockUpdates(int x, int y, int z, long updateTime) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		pending.add(new PointLongPair(x, y, z, updateTime));
	}
	
	public void queueBlockUpdates(int x, int y, int z, InsertionPolicy policy, long updateTime) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		pending.add(new PointPolicyLongTriplet(x, y, z, policy, updateTime));
	}
	
	public void queueBlockUpdates(int x, int y, int z, long updateTime, Object hint) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		pending.add(new PointLongObjectTriplet(x, y, z, updateTime, hint));
	}
	
	public void queueBlockUpdates(int x, int y, int z, InsertionPolicy policy, long updateTime, Object hint) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		pending.add(new PointPolicyLongObjectQuartet(x, y, z, policy, updateTime, hint));
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
			if (!remove(dm)) {
				throw new IllegalStateException("Expected update not present when removing all updates for chunk " + c);
			}
		}
		return false;
	}
	
	public void commitPending(long currentTime) {
		TickStage.checkStage(TickStage.FINALIZE, regionThread);
		List<DynamicBlockUpdate> l;
		while ((l = pendingLists.poll()) != null) {
			for (DynamicBlockUpdate update : l) {
				add(update, InsertionPolicy.FORCE_REPLACE_NONE);
			}
		}
		processed.clear();
		PointAlone p;
		while ((p = resetPending.poll()) != null) {
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
				long nextUpdate = dm.onPlacement(b, region, currentTime);
				if (nextUpdate > 0) {
					add(new DynamicBlockUpdate(bx, by, bz, nextUpdate, currentTime, null), InsertionPolicy.FORCE_REPLACE_NONE);
				}
			}
		}
		while ((p = pending.poll()) != null) {
			int bx = p.getBlockX() & Region.BLOCKS.MASK;
			int by = p.getBlockY() & Region.BLOCKS.MASK;
			int bz = p.getBlockZ() & Region.BLOCKS.MASK;
			
			Object hint = p.getHint();
			long updateTime = p.getUpdateTime();
			
			InsertionPolicy policy = (p instanceof HasPolicy) ? ((HasPolicy)p).getPolicy() : null;
			
			add(new DynamicBlockUpdate(bx, by, bz, updateTime, currentTime, hint), policy);
			
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
		TickStage.checkStage(TickStage.FINALIZE, regionThread);
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
		Vector3[] range = (force) ? zeroVector3Array : dm.maxRange();
		if (range == null || range.length < 1) {
			range = zeroVector3Array;
		}
		Vector3 rangeHigh = range[0];
		Vector3 rangeLow = range.length < 2 ? range[0] : range[1];

		int rhx = (int)rangeHigh.getX();
		int rhy = (int)rangeHigh.getY();
		int rhz = (int)rangeHigh.getZ();
		if (rhx < 0 || rhy < 0 || rhz < 0) {
			throw new IllegalArgumentException("Max range values must be greater or equal to 0");
		}
		int rlx = (int)rangeLow.getX();
		int rly = (int)rangeLow.getY();
		int rlz = (int)rangeLow.getZ();
		if (rlx < 0 || rly < 0 || rlz < 0) {
			throw new IllegalArgumentException("Max range values must be greater or equal to 0");
		}
		int maxx = bx + rhx;
		int maxy = by + rhy;
		int maxz = bz + rhz;
		int minx = bx - rlx;
		int miny = by - rly;
		int minz = bz - rlz;
		int rs = Region.BLOCKS.SIZE;
		if (maxx >= rs || maxy >= rs || maxz >= rs || minx < 0 || miny < 0 || minz < 0) {
			return false;
		}

		long nextUpdate = dm.update(b, region, update.getNextUpdate(), update.getLastUpdate(), update.getHint());
		if (nextUpdate > 0) {
			add(new DynamicBlockUpdate(bx, by, bz, nextUpdate, currentTime, null), InsertionPolicy.FORCE_REPLACE_NONE);
		}
		return true;
	}
	
	public DynamicBlockUpdate getNextUpdate(long currentTime) {
		TickStage.checkStage(TickStage.FINALIZE, regionThread);
		if (queuedUpdates.isEmpty()) {
			return null;
		}

		DynamicBlockUpdate first = queuedUpdates.first();
		if (first == null) {
			return null;
		}

		if (first.getNextUpdate() > currentTime) {
			return null;
		}

		if (!remove(first)) {
			throw new IllegalStateException("queued updates for dynamic block updates violated threading rules");
		}

		return first;
	}
	
	private boolean add(DynamicBlockUpdate update, InsertionPolicy policy) {
		boolean alreadyPresent = false;
		int key = update.getPacked();
		DynamicBlockUpdate previous = blockToUpdateMap.get(key);
		boolean removedUpdate = false;
		if (previous != null) {
			alreadyPresent = true;
			if (policy != null) {
				if (policy.replaceAll()) {
					removeAll(key);
				} else if (!policy.replaceNone()) {
					DynamicBlockUpdate current = previous;
					while (current != null) {
						if (policy.canReplace(update.getNextUpdate(), current.getNextUpdate())) {
							remove(current);
							removedUpdate = true;
							previous = blockToUpdateMap.get(key);
							current = previous;
							continue;
						}
						current = current.getNext();
					}
				}
			}
			
		} else {
			// Effectively did
			removedUpdate = true;
		}
		if (policy == null || policy.isForce() || removedUpdate) {
			if (previous != null) {
				DynamicBlockUpdate newRoot = previous.add(update);
				if (newRoot != previous) {
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
		}
		return alreadyPresent;
	}
	
	/**
	 * Removes a specific update
	 * 
	 * @param update the update to remove
	 * @return true if the update was removed
	 */
	private boolean remove(DynamicBlockUpdate update) {
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
		return removed;
	}
		
	/**
	 * Removes all updates with at the given block location
	 * 
	 * @param update the packed value for the block location
	 * @return true if at least one update was removed
	 */
	private boolean removeAll(int packed) {
		DynamicBlockUpdate previous = blockToUpdateMap.remove(packed);
		if (previous == null) {
			return false;
		}

		while (previous != null) {
			if (!queuedUpdates.remove(previous)) {
				throw new IllegalStateException("Dynamic block update missing from queue when removed");
			}
			int previousPacked = previous.getChunkPacked();
			HashSet<DynamicBlockUpdate> chunkSet = chunkToUpdateMap.get(previousPacked);
			if (chunkSet == null || !chunkSet.remove(previous)) {
				throw new IllegalStateException("Dynamic block update missing from chunk when removed");
			}

			if (chunkSet.size() == 0) {
				if (chunkToUpdateMap.remove(previousPacked) == null) {
					throw new IllegalStateException("Removing updates for dynamic block updates violated threading rules");
				}
			}
			previous = previous.getNext();
		}
		return true;
	}
}
