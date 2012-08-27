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
package org.spout.engine.util;

import org.spout.api.Spout;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncExecutorUtils;

public class TicklockMonitor extends Thread {
	
	public TicklockMonitor() {
		super("Tick Monitor");
		setDaemon(true);
	}
	
	@Override
	public void run() {

		long tickPeriod = SpoutScheduler.PULSE_EVERY;
		long threshold = tickPeriod * 200;
		boolean dead = false;
		long lastUpTime = 0;
		while (!dead && !interrupted()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				dead = true;
			}
			
			long tickTime = Spout.getEngine().getScheduler().getTickTime();
			long upTime = Spout.getEngine().getScheduler().getUpTime();
			
			if (tickTime > threshold && upTime != lastUpTime) {
				Spout.getLogger().info("Current Tick Time exceeds " + (threshold / 1000) + " seconds");
				AsyncExecutorUtils.dumpAllStacks();
				AsyncExecutor e = AsyncExecutorUtils.getWaitingExecutor();
				if (e != null && e instanceof Thread) {
					Thread t = (Thread)e;
					Spout.getLogger().info("pulseJoinAll is waiting on " + t.getName());
					AsyncExecutorUtils.dumpStackTrace(t);
					if (!t.isAlive()) {
						Spout.getLogger().info("Thread is dead");
					}
				}
				lastUpTime = upTime;
			} else {
				//Spout.getLogger().info("Current tick time: " + tickTime);
			}
		}
	}
}
