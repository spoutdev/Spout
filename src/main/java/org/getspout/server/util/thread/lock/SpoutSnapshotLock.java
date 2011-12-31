package org.getspout.server.util.thread.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.getspout.api.plugin.Plugin;
import org.getspout.api.scheduler.SnapshotLock;

public class SpoutSnapshotLock implements SnapshotLock {

	private ReentrantReadWriteLock lock;
	
	private ConcurrentHashMap<Plugin, LockInfo> locks = new ConcurrentHashMap<Plugin, LockInfo>();
	
	private static final long serialVersionUID = 1L;

	public void readLock(Plugin plugin) {
		lock.readLock().lock();
		addLock(plugin);
	}
	
	public boolean readTryLock(Plugin plugin) {
		boolean success = lock.readLock().tryLock();
		if (success) {
			addLock(plugin);
		}
		return success;
	}
	
	public void readUnlock(Plugin plugin) {
		lock.readLock().unlock();
		addLock(plugin);
	}
	
	public boolean writeLock(int delay) {
		boolean success;
		try {
			success = lock.writeLock().tryLock((long)delay, TimeUnit.MILLISECONDS);
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
			if (info.locks > 0 && (currentTime - info.oldestLock) > threshold) {
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
		
		public final long oldestLock;;
		public final int locks;
	}
	
}
