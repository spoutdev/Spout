package org.spout.api.io.regionfile;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SRFReentrantReadWriteLock {

	private final Lock readLock;
	private final Lock writeLock;
	
	public SRFReentrantReadWriteLock(AtomicInteger lockCounter) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = new SRFBlockLock(lock.readLock(), lockCounter);
		this.writeLock = new SRFBlockLock(lock.writeLock(), lockCounter);
	}
	
	public Lock readLock() {
		return readLock;
	}
	
	public Lock writeLock() {
		return writeLock;
	}
	
}
