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
import org.spout.engine.scheduler.SchedulerSyncExecutorThread;

public class SpoutSnapshotLock implements SnapshotLock {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final ConcurrentHashMap<Object, LockInfo> locks = new ConcurrentHashMap<Object, LockInfo>();
	private final ConcurrentHashMap<String, Integer> coreTasks = new ConcurrentHashMap<String, Integer>();
	private final ConcurrentHashMap<Thread, Integer> coreLockingThreads = new ConcurrentHashMap<Thread, Integer>();

 	@Override
	public void readLock(Object plugin) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return;
 		}
		lock.readLock().lock();
		addLock(plugin);
	}
	
	public void coreReadLock(String taskName) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return;
 		}
		if (taskName == null) {
			throw new IllegalArgumentException("Taskname may not be null");
		}
		lock.readLock().lock();
		incrementCoreCounter(taskName);
	}

	@Override
	public boolean readTryLock(Object plugin) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return true;
 		}
		boolean success = lock.readLock().tryLock();
		if (success) {
			addLock(plugin);
		}
		return success;
	}
	
	public boolean coreReadTryLock(String taskName) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return true;
 		}
		if (taskName == null) {
			throw new IllegalArgumentException("Taskname may not be null");
		}
		boolean success = lock.readLock().tryLock();
		if (success) {
			incrementCoreCounter(taskName);
		}
		return success;
	}
	
	@Override
	public boolean isWriteLocked() {
		if (!lock.readLock().tryLock()) {
			return true;
		}

		try {
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void readUnlock(Object plugin) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return;
 		}
		lock.readLock().unlock();
		removeLock(plugin);
	}
	
	public void coreReadUnlock(String taskName) {
 		if (Thread.currentThread() instanceof SchedulerSyncExecutorThread) {
 			return;
 		}
		lock.readLock().unlock();
		decrementCoreCounter(taskName);
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

	public List<Object> getLockingPlugins(int threshold) {
		ArrayList<Object> plugins = new ArrayList<Object>();
		Set<Entry<Object, LockInfo>> entries = locks.entrySet();

		long currentTime = System.currentTimeMillis();

		for (Entry<Object, LockInfo> e : entries) {
			LockInfo info = e.getValue();
			if (info.locks > 0 && currentTime - info.oldestLock > threshold) {
				plugins.add(e.getKey());
			}
		}
		return plugins;
	}
	
	public Set<String> getLockingTasks() {
		return coreTasks.keySet();
	}
	
	public Set<Thread> getCoreLockingThreads() {
		return coreLockingThreads.keySet();
	}

	public void writeUnlock() {
		lock.writeLock().unlock();
	}

	private void addLock(Object plugin) {
		boolean success = false;

		long currentTime = System.currentTimeMillis();

		while (!success) {
			final LockInfo oldLockInfo = locks.get(plugin);

			final LockInfo newLockInfo;
			if (oldLockInfo == null || oldLockInfo.locks == 0) {
				newLockInfo = new LockInfo(currentTime, 1);
			} else {
				newLockInfo = new LockInfo(oldLockInfo.oldestLock, oldLockInfo.locks + 1);
			}
			if (oldLockInfo == null) {
				success = locks.putIfAbsent(plugin, newLockInfo) == null;
			} else {
				success = locks.replace(plugin, oldLockInfo, newLockInfo);
			}
		}
	}

	private void removeLock(Object plugin) {
		boolean success = false;

		while (!success) {
			final LockInfo oldLockInfo = locks.get(plugin);
			if (oldLockInfo == null) {
				if (plugin instanceof Plugin) {
					Plugin p = (Plugin) plugin;
					throw new IllegalArgumentException("Attempted to remove a lock for a plugin with no previously added lock, " + p.getName());
				} else {
					throw new IllegalArgumentException("Attempted to remove a lock for a plugin with no previously added lock, " + plugin);
				}
			}

			final LockInfo newLockInfo = new LockInfo(oldLockInfo.oldestLock, oldLockInfo.locks - 1);
			success = locks.replace(plugin, oldLockInfo, newLockInfo);
		}
	}
	
	private void incrementCoreCounter(String taskName) {
		boolean success = false;
		while (!success) {
			Integer i = coreTasks.get(taskName);
			if (i == null) {
				success = coreTasks.putIfAbsent(taskName, 1) == null;
			} else {
				success = coreTasks.replace(taskName, i, i + 1);
			}
		}
		
		success = false;
		while (!success) {
			Thread t = Thread.currentThread();
			Integer i = coreLockingThreads.get(t);
			if (i == null) {
				success = coreLockingThreads.putIfAbsent(t, 1) == null;
			} else {
				success = coreLockingThreads.replace(t, i, i + 1);
			}
		}
	}
	
	private void decrementCoreCounter(String taskName) {
		boolean success = false;
		while (!success) {
			Integer i = coreTasks.get(taskName);
			if (i == null || i <= 0) {
				throw new IllegalStateException("Attempting to unlock a core read lock which was already unlocked, " + taskName);
			} else if (i.equals(1)) {
				success = coreTasks.remove(taskName, i);
			} else {
				success = coreTasks.replace(taskName, i, i - 1);
			}
		}
		
		success = false;
		while (!success) {
			Thread t = Thread.currentThread();
			Integer i = coreLockingThreads.get(t);
			if (i == null || i <= 0) {
				throw new IllegalStateException("Attempting to unlock a core read lock which was already unlocked, " + taskName);
			} else if (i.equals(1)) {
				success = coreLockingThreads.remove(t, i);
			} else {
				success = coreLockingThreads.replace(t, i, i - 1);
			}
		}
	}

	private class LockInfo {
		public LockInfo(long oldestLock, int locks) {
			this.oldestLock = oldestLock;
			this.locks = locks;
		}

		public final long oldestLock;
		public final int locks;
	}
}
