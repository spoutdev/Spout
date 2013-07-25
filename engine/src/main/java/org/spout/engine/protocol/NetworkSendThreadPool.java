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
package org.spout.engine.protocol;

import org.spout.api.Spout;

public class NetworkSendThreadPool {
	private static final int POOL_MASK = 0xF;
	private static final NetworkSendThread[] pool;

	static {
		pool = new NetworkSendThread[POOL_MASK + 1];
		for (int i = 0; i < pool.length; i++) {
			pool[i] = new NetworkSendThread(i);
		}
	}

	public static NetworkSendThread getNetworkThread(int playerId) {
		return pool[hash(playerId) & POOL_MASK];
	}

	public static void interrupt() {
		for (int i = 0; i < pool.length; i++) {
			pool[i].interrupt();
		}
	}

	public static void shutdown() {
		for (int i = 0; i < pool.length; i++) {
			pool[i].interrupt();
		}
		for (int i = 0; i < pool.length; i++) {
			try {
				pool[i].interruptAndJoin();
			} catch (InterruptedException e) {
				Spout.getLogger().info("Thread interrupted when waiting for NetworkSendPool to shutdown");
			}
		}
	}

	// Taken from HashMap
	private static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}
}
