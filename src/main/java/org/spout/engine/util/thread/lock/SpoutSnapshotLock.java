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
package org.spout.engine.util.thread.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.SnapshotLock;

public class SpoutSnapshotLock implements SnapshotLock {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ConcurrentHashMap<Plugin, LockInfo> locks = new ConcurrentHashMap<Plugin, LockInfo>();

	@Override
	public void readLock(Plugin plugin) {
		lock.readLock().lock();
		addLock(plugin);
	}

	@Override
	public boolean readTryLock(Plugin plugin) {
		boolean success = lock.readLock().tryLock();
		if (success) {
			addLock(plugin);
		}
		return success;
	}

	@Override
	public void readUnlock(Plugin plugin) {
		lock.readLock().unlock();
		addLock(plugin);
	}

	public boolean writeLock(int delay) {
		boolean success;
		try {
			success = lock.writeLock().tryLock(delay, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			success = false;
		}
		return success;
	}

	public List<Plugin> getLockingPlugins(int threshold) {
		ArrayList<Plugin> plugins = new ArrayList<Plugin>();
		Set<Entry<Plugin, LockInfo>> entries = locks.entrySet();

		long currentTime = System.currentTimeMillis();

		for (Entry<Plugin, LockInfo> e : entries) {
			LockInfo info = e.getValue();
			if (info.locks > 0 && currentTime - info.oldestLock > threshold) {
				plugins.add(e.getKey());
			}
		}
		return plugins;
	}

	public void writeUnlock() {
		lock.writeLock().unlock();
	}

	public void addLock(Plugin plugin) {
		boolean success = false;

		long currentTime = System.currentTimeMillis();

		while (!success) {
			LockInfo oldLockInfo = locks.get(plugin);

			LockInfo newLockInfo;
			if (oldLockInfo == null || oldLockInfo.locks == 0) {
				newLockInfo = new LockInfo(currentTime, 1);
			} else {
				newLockInfo = new LockInfo(oldLockInfo.oldestLock, oldLockInfo.locks + 1);
			}
			// FIXME: oldLockInfo could be null here, which would raise an exception.
			success = locks.replace(plugin, oldLockInfo, newLockInfo);
		}
	}

	public void removeLock(Plugin plugin) {
		boolean success = false;

		while (!success) {
			LockInfo oldLockInfo = locks.get(plugin);

			LockInfo newLockInfo;
			if (oldLockInfo == null) {
				throw new IllegalArgumentException("Attempted to remove a lock for a plugin with no previously added lock");
			} else {
				newLockInfo = new LockInfo(oldLockInfo.oldestLock, oldLockInfo.locks - 1);
			}
			success = locks.replace(plugin, oldLockInfo, newLockInfo);
		}
	}

	private class LockInfo {
		public LockInfo(long oldestLock, int locks) {
			this.oldestLock = oldestLock;
			this.locks = locks;
		}

		public final long oldestLock;
		;
		public final int locks;
	}
}
