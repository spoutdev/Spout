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
package org.spout.engine.world.physics;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.range.EffectIterator;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.IntVector3;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class PhysicsQueue {
	
	private final static int localStages = TickStage.DYNAMIC_BLOCKS | TickStage.PHYSICS;
	private final static int globalStages = TickStage.GLOBAL_DYNAMIC_BLOCKS | TickStage.GLOBAL_PHYSICS;
	
	private final static int MASK = ~Chunk.BLOCKS.MASK;
	
	private final SpoutRegion region;
	private final SpoutChunk chunk;
	private final Thread regionThread;
	@SuppressWarnings("unused")
	private final Thread mainThread;
	private final AtomicBoolean localActive = new AtomicBoolean(false);
	private final AtomicBoolean globalActive = new AtomicBoolean(false);
	
	private final ConcurrentLinkedQueue<PhysicsUpdate> asyncQueue = new ConcurrentLinkedQueue<PhysicsUpdate>();
	private final UpdateQueue updateQueue = new UpdateQueue();
	private final UpdateQueue multiRegionQueue = new UpdateQueue();
	
	public PhysicsQueue(SpoutChunk chunk) {
		this.region = chunk.getRegion();
		this.chunk = chunk;
		this.regionThread = region.getExceutionThread();
		this.mainThread = ((SpoutScheduler)Spout.getScheduler()).getMainThread();
	}
	
	public boolean commitAsyncQueue() {
		boolean updated = false;
		PhysicsUpdate update;
		EffectIterator ei = new EffectIterator();
		while ((update = asyncQueue.poll()) != null) {
			updated = true;
			update.getRange().initEffectIterator(ei);
			int x = update.getX();
			int y = update.getY();
			int z = update.getZ();
			while (ei.hasNext()) {
				IntVector3 v = ei.next();
				int ox = x + v.getX();
				int oy = y + v.getY();
				int oz = z + v.getZ();
				if ((ox & MASK) == (x & MASK) && (oy & MASK) == (y & MASK) && (oz & MASK) == (z & MASK)) {
					queueForUpdate(ox, oy, oz, update.getOldMaterial(), update.getSource());
				} else if (ox >= 0 && ox < Region.BLOCKS.SIZE && oy >= 0 && oy < Region.BLOCKS.SIZE && oz >= 0 && oz < Region.BLOCKS.SIZE) {
					region.updateBlockPhysics(ox, oy, oz, update.getOldMaterial(), update.getSource());
				} else {
					region.getWorld().queueBlockPhysics(region.getBlockX() + ox, region.getBlockY() + oy, region.getBlockZ() + oz, EffectRange.THIS, update.getOldMaterial(), update.getSource());
				}
			}
		}
		return updated;
	}
	
	public void queueForUpdateAsync(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial, Source source) {
		asyncQueue.add(new PhysicsUpdate(x, y, z, range, oldMaterial, source));
		registerActive();
	}
	
	public void queueForUpdate(int x, int y, int z, BlockMaterial oldMaterial, Source source) {
		checkStages();
		updateQueue.add(x, y, z, oldMaterial, source);
		registerActive();
	}
	
	public void queueForUpdateMultiRegion(int x, int y, int z, BlockMaterial oldMaterial, Source source) {
		checkStages();
		multiRegionQueue.add(x, y, z, oldMaterial, source);
		registerActive();
	}
	
	public void setInactive(boolean local) {
		if (local) {
			localActive.set(false);
		} else {
			globalActive.set(false);
		}
	}
	
	public void registerActive() {
		if (localActive.compareAndSet(false, true)) {
			region.setPhysicsActive(chunk, true);
		}
		if (globalActive.compareAndSet(false, true)) {
			region.setPhysicsActive(chunk, false);
		}
	}
	
	public UpdateQueue getUpdateQueue() {
		return updateQueue;
	}
	
	public UpdateQueue getMultiRegionQueue() {
		return multiRegionQueue;
	}
	
	private void checkStages() {
		TickStage.checkStage(globalStages, localStages, regionThread);
	}

}
