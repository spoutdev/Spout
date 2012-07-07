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

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.RandomBlockMaterial;
import org.spout.api.scheduler.TaskPriority;

public class RandomUpdateTask implements Runnable{
	protected static final int TICK_DELAY = 20;
	protected static final int NUM_UPDATES = 1;
	private final WeakReference<Chunk> chunk;
	private final Random rand = new Random();
	protected RandomUpdateTask(Chunk chunk) {
		this.chunk = new WeakReference<Chunk>(chunk);
	}

	@Override
	public void run() {
		Chunk chunk = this.chunk.get();
		if (chunk != null) {
			if (chunk.isLoaded() && chunk.isPopulated()) {
				for (int tick = 0; tick < NUM_UPDATES; tick++) {
					int randomX = chunk.getBlockX(rand);
					int randomY = chunk.getBlockY(rand);
					int randomZ = chunk.getBlockZ(rand);
					BlockMaterial material = chunk.getBlockMaterial(randomX, randomY, randomZ);
					if (material instanceof RandomBlockMaterial) {
						try {
							((RandomBlockMaterial)material).onRandomTick(chunk.getWorld(), randomX, randomY, randomZ);
						} catch (Exception e) {
							Spout.getLogger().log(Level.SEVERE, "Exception ticking random block material [" + material.getClass().getSimpleName() + "]", e);
						}
					}
				}
			}
			chunk.getRegion().getTaskManager().scheduleSyncDelayedTask(Spout.getEngine(), this, TICK_DELAY, TaskPriority.LOWEST);
		}
	}

}
