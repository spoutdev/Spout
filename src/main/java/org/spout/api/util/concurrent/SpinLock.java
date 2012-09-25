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
package org.spout.api.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
* A non-reentrant spin lock.<br>
* <br>
* The lock will spin 100 times before falling back to using wait/notify.
*/

public class SpinLock implements Lock {
	
	private static int MAX_SPINS = 100;
	
	private AtomicBoolean locked = new AtomicBoolean();
	private AtomicInteger waiting = new AtomicInteger();

	@Override
	public void lock() {
		for (int i = 0; i < MAX_SPINS; i++) {
			if (tryLock()) {
				return;
			}
		}

		boolean interrupted = false;
		boolean success = false;

		try {
			while (!success) {
				try {
					waitLock();
					success = true;
				} catch (InterruptedException ie) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		for (int i = 0; i < MAX_SPINS; i++) {
			if (tryLock()) {
				return;
			}
		}
		waitLock();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		long endTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit) + 1;
		boolean timedOut = false;
		while (!tryLock()) {
			timedOut = System.currentTimeMillis() >= endTime;
			if (timedOut) {
				break;
			}
		}
		return !timedOut;
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean tryLock() {
		return locked.compareAndSet(false, true);
	}
	
	private void waitLock() throws InterruptedException {
		waiting.incrementAndGet();
		try {
			synchronized (waiting) {
				while (!tryLock()) {
					waiting.wait();
				}
			}
		} finally {
			waiting.decrementAndGet();
		}
	}
	
	@Override
	public void unlock() {
		if (!locked.compareAndSet(true, false)) {
			throw new IllegalStateException("Attempt to unlock lock when it isn't locked");
		}
		if (!waiting.compareAndSet(0, 0)) {
			synchronized (waiting) {
				waiting.notifyAll();
			}
		}
	}

}
