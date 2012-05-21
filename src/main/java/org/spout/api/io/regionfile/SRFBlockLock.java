/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.io.regionfile;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SRFBlockLock implements Lock {
	
	private final AtomicInteger lockCounter;
	private final Lock lock;

	public SRFBlockLock(Lock lock, AtomicInteger lockCounter) {
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
