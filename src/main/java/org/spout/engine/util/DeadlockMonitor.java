/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.spout.api.Spout;

public class DeadlockMonitor extends Thread {
	@Override
	public void run() {

		boolean dead = false;
		while (!dead && !interrupted()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				dead = true;
			}
			ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
			long[] ids = tmx.findDeadlockedThreads();
			if (ids != null) {
				Spout.getLogger().info("Checking for deadlocks");
				ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
				Spout.getLogger().severe("The following threads are deadlocked:");
				for (ThreadInfo ti : infos) {
					Spout.getLogger().severe(ti.toString());
				}
			}
		}
	}
}
