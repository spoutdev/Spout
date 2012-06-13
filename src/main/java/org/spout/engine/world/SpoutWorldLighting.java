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

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;

public class SpoutWorldLighting extends Thread implements Source {

	public final SpoutWorldLightingModel skyLight;
	public final SpoutWorldLightingModel blockLight;
	private final SpoutWorld world;
	private boolean running = false;

	public boolean isRunning() {
		return this.running;
	}

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		this.world = world;
		this.skyLight = new SpoutWorldLightingModel(this, true);
		this.blockLight = new SpoutWorldLightingModel(this, false);
	}

	public void abort() {
		this.running = false;
	}

	public SpoutWorld getWorld() {
		return this.world;
	}

	@Override
	public void run() {
		this.running = SpoutConfiguration.LIGHTING_ENABLED.getBoolean();
		SpoutSnapshotLock lock = (SpoutSnapshotLock)Spout.getEngine().getScheduler().getSnapshotLock();
		while (this.running) {
			boolean updated = false;
			// Bergerkiller, ideally, these 2 methods would have a max time of 5-10ms
			// Better to do 10 calls of 2ms each, and release the lock between them, than 1 call of 20ms.
			lock.coreReadLock();
			try {
				updated = this.skyLight.resolve() || this.blockLight.resolve();
			} finally {
				lock.coreReadUnlock();
			}
			if (!updated) {
				this.skyLight.reportChanges();
				this.blockLight.reportChanges();
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {}
			}
		}
	}
}
