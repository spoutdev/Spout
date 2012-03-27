package org.spout.api.io.regionfile;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleRegionFileBlockLock implements Lock {
	
	private final AtomicInteger lockCounter;
	private final Lock lock;

	public SimpleRegionFileBlockLock(Lock lock, AtomicInteger lockCounter) {
		this.lock = lock;
		this.lockCounter = lockCounter;
	}
	
	@Override
	public void lock() {
		incrementLockCounter();
		lock.lock();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException("");
	
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void unlock() {
		lock.unlock();
		decrementLockCounter();
	}
	
	/**
	 * Increments the lock counter.<br>
	 * 
	 * @return the number of blocks locked or FILE_CLOSED
	 */
	private int incrementLockCounter() {
		while (true) {
			int oldValue = this.lockCounter.get();
			
			if (oldValue == SimpleRegionFile.FILE_CLOSED) {
				return SimpleRegionFile.FILE_CLOSED;
			}
			
			int newValue = oldValue + 1;
			if (this.lockCounter.compareAndSet(oldValue, newValue)) {
				return newValue;
			}
		}
	}
	
	/**
	 * Increments the lock counter.<br>
	 * 
	 * @return the number of blocks locked or FILE_CLOSED
	 */
	private int decrementLockCounter() {
		while (true) {
			int oldValue = this.lockCounter.get();
			
			if (oldValue == SimpleRegionFile.FILE_CLOSED) {
				return SimpleRegionFile.FILE_CLOSED;
			} else if (oldValue <= 0) {
				throw new RuntimeException("Attempt made to decrement lock counter below zero");
			}
			
			int newValue = oldValue - 1;
			if (this.lockCounter.compareAndSet(oldValue, newValue)) {
				return newValue;
			}
		}
	}

}
