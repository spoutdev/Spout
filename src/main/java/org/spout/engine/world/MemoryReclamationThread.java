/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.world;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.spout.api.Spout;

public class MemoryReclamationThread extends Thread{
	private int previousPlayers = 0;
	private final AtomicInteger numPlayers = new AtomicInteger(0);

	public MemoryReclamationThread() {
		super("Memory reclaimation thread");
		setDaemon(true);
	}

	public void addPlayer() {
		numPlayers.getAndIncrement();
	}

	public void removePlayer() {
		numPlayers.getAndDecrement();
	}

	@Override
	public void run() {
		boolean g1 = false;
		List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean gc : gcs) {
			if (gc.isValid()) {
				if (gc.getName().contains("G1")) {
					g1 = true;
					break;
				}
			}
		}

		if (!g1) {
			//Garbage collection with CMS will actually increase memory used :(
			Spout.getLogger().log(Level.INFO, "G1 Garbage Collector is not set, memory reclamation is not possible.");
			return;
		} else {
			Spout.getLogger().log(Level.INFO, "G1 Garbage Collector is  set, memory reclamation enabled.");
		}

		while(!this.isInterrupted()) {
			try {
				sleep(60000);
			} catch (InterruptedException e) {
				break;
			}

			int numPlayers = this.numPlayers.get();
			if (previousPlayers != numPlayers) {
				if (previousPlayers > numPlayers) {
					System.gc();
				}
				previousPlayers = numPlayers;
			}
			
		}
	}
}
