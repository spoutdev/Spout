/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements an optimistic lock
 */
public class OptimisticReadWriteLock {
	private final AtomicInteger waiting = new AtomicInteger(0);
	private final AtomicInteger sequence = new AtomicInteger(0);
	public final static int UNSTABLE = 1;

	/*
	 * Timeout in ns until spin locks switch to standard lock
	 */
	private final static int SPIN_TIMEOUT = 1000;

	/**
	 * Attempts to read lock the lock.
	 *
	 * @return the sequence number, or OptimisticReadWriteLock.UNSTABLE on fail
	 */
	public int tryReadLock() {
		return sequence.get();
	}

	/**
	 * Read locks the lock, and waits if necessary.
	 *
	 * @return the sequence number
	 * @throws InterruptedException if the thread is interrupted while waiting
	 */
	public int readLock() throws InterruptedException {
		int seq;
		if ((seq = tryReadLock()) != UNSTABLE) {
			return seq;
		} else {
			long startTime = System.nanoTime();
			long currentTime = System.nanoTime();
			while (currentTime - startTime < SPIN_TIMEOUT) {
				if ((seq = tryReadLock()) != UNSTABLE) {
					return seq;
				}
				currentTime = System.nanoTime();
			}
			waiting.incrementAndGet();
			try {
				synchronized(this) {
					while (true) {
						seq = sequence.get();
						if ((seq = tryReadLock()) != UNSTABLE) {
							return seq;
						}
						wait();
					}
				}
			} finally {
				waiting.decrementAndGet();
			}
		}
	}

	/**
	 * Unlocks the lock after reading and returns true if no changes were made during the read.  This
	 * method has no effect on the lock and only indicates if a write operation occurred while the read
	 * lock was locked.
	 *
	 * @param sequence the sequence number when the lock was read locked
	 * @return true if the sequence number has not changed and the lock is not in the UNSTABLE state
	 */
	public boolean readUnlock(int sequence) {
		int seq = this.sequence.get();
		return seq != UNSTABLE && seq == sequence;
	}

	/**
	 * Attempts to write lock the lock.
	 *
	 * @return the old sequence number, or OptimisticReadWriteLock.UNSTABLE on fail
	 */
	public int tryWriteLock() {
		return sequence.getAndSet(UNSTABLE);
	}

	/**
	 * Write locks the lock, and waits if necessary.
	 *
	 * @throws InterruptedException if the thread is interrupted while waiting
	 */
	public int writeLock() throws InterruptedException {
		int seq;
		if ((seq = tryWriteLock()) != UNSTABLE) {
			return seq;
		} else {
			long startTime = System.nanoTime();
			long currentTime = System.nanoTime();
			while (currentTime - startTime < SPIN_TIMEOUT) {
				if ((seq = tryWriteLock()) != UNSTABLE) {
					return seq;
				}
				currentTime = System.nanoTime();
			}
			waiting.incrementAndGet();
			try {
				synchronized(this) {
					while (true) {
						if ((seq = tryWriteLock()) != UNSTABLE) {
							return seq;
						}
						wait();
					}
				}
			} finally {
				waiting.decrementAndGet();
			}
		}
	}

	/**
	 * Unlocks the lock after writing.
	 *
 	 * @param sequence the sequence number when the lock was write locked
	 */
	public void writeUnlock(int sequence) {
		try {
			if (!this.sequence.compareAndSet(UNSTABLE, sequence + 2)) {
				throw new IllegalStateException("Write unlock called when the write lock was not active");
			}
		} finally {
			if (waiting.get() > 0) {
				synchronized(this) {
					notifyAll();
				}
			}
		}
	}
}
