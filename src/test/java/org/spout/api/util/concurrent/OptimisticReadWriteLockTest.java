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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptimisticReadWriteLockTest {
	private final int LENGTH = 1000000;

	private OptimisticReadWriteLock lock = new OptimisticReadWriteLock();

	int seqRead;
	int seqWrite;

	@Test
	public void testLock() {
		long startTime = System.nanoTime();

		for (int i = 0; i < LENGTH; i++) {
			int seq = lock.writeLock();
			lock.writeUnlock(seq);
		}

		long endTime = System.nanoTime();

		System.out.println("Time for " + LENGTH + " write lock/unlocks was " + (endTime - startTime) + "ns");

		readLock();

		writeLock();

		readUnlock(false);

		tryWriteLock();

		writeUnlock();

		writeLock();

		tryReadLock();

		writeUnlock();

		readLock();

		writeLock();

		writeUnlock();

		readUnlock(false);

		readLock();

		readUnlock(true);

	}

	private void readLock() {
		System.out.println("Read locking lock");
		seqRead = lock.readLock();
		assertTrue("Read lock unsuccessful when write lock inactive", seqRead != OptimisticReadWriteLock.UNSTABLE);
		System.out.println("- Read lock successful");
		System.out.println();
	}

	private void tryReadLock() {
		System.out.println("Trying to read lock");
		int s = lock.tryReadLock();
		assertTrue("Read lock successful when write lock active", s == OptimisticReadWriteLock.UNSTABLE);
		System.out.println("- Try read lock failed (as expected)");
		System.out.println();
	}

	private void readUnlock(boolean successExpected) {
		System.out.println("Read unlocking lock");
		boolean success = lock.readUnlock(seqRead);
		if (successExpected) {
			assertTrue("Read unlock unsuccessful when data was stable", success);
		} else {
			assertTrue("Read unlock successful when data was potentially changed", !success);
		}
		System.out.println("- Read unlock success: " + success + " (as expected)");
		System.out.println();
	}

	private void writeLock() {
		System.out.println("Write locking lock");
		seqWrite = lock.writeLock();
		assertTrue("Write lock unsuccessful, which should not be possible", seqWrite != OptimisticReadWriteLock.UNSTABLE);
		System.out.println("- Write lock successful");
		System.out.println();
	}

	private void writeUnlock() {
		System.out.println("Write unlocking lock");
		lock.writeUnlock(seqWrite);
		System.out.println();
	}	
	private void tryWriteLock() {
		System.out.println("Trying to write lock");
		int w = lock.tryWriteLock();
		assertTrue("Write lock successful when write lock already active", w == OptimisticReadWriteLock.UNSTABLE);
		System.out.println("- Write lock attempt failed (as expected)");
		System.out.println();
	}
}
