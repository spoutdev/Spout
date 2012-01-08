/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.util.thread;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class AsyncExecutorUtils {

	/**
	 * Waits for a list of ManagedThreads to complete a pulse
	 *
	 * @param threads the threads to join for
	 * @param timeout how long to wait, or 0 to wait forever
	 */
	public static void pulseJoinAll(List<AsyncExecutor> executors, long timeout) throws TimeoutException, InterruptedException {
		ThreadsafetyManager.checkMainThread();

		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + timeout;
		boolean waitForever = timeout == 0;

		if (timeout < 0) {
			throw new IllegalArgumentException("Negative timeouts are not allowed (" + timeout + ")");
		}

		boolean done = false;
		while (!done && (endTime > currentTime || waitForever)) {
			done = false;
			while (!done && (endTime > currentTime || waitForever)) {
				done = true;
				for (AsyncExecutor e : executors) {
					currentTime = System.currentTimeMillis();
					if (endTime <= currentTime && !waitForever) {
						break;
					}
					if (!e.isPulseFinished()) {
						done = false;
						e.pulseJoin(endTime - currentTime);
					}
				}
			}
			try {
				for (AsyncExecutor e : executors) {
					e.disableWake();
				}
				done = true;
				for (AsyncExecutor e : executors) {
					if (!e.isPulseFinished()) {
						done = false;
						break;
					}
				}
			} finally {
				for (AsyncExecutor e : executors) {
					e.enableWake();
				}
			}
		}

		if (endTime <= currentTime && !waitForever) {
			throw new TimeoutException("pulseJoinAll timed out");
		}

	}

}
