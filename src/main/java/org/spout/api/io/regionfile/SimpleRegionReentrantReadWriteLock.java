package org.spout.api.io.regionfile;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleRegionReentrantReadWriteLock {

	private final Lock readLock;
	private final Lock writeLock;
	
	public SimpleRegionReentrantReadWriteLock(AtomicInteger lockCounter) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = new SimpleRegionFileBlockLock(lock.readLock(), lockCounter);
		this.writeLock = new SimpleRegionFileBlockLock(lock.writeLock(), lockCounter);
	}
	
	public Lock readlock() {
		return readLock;
	}
	
	public Lock writelock() {
		return writeLock;
	}
	
}
