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
package org.spout.api.util.map.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.datatable.DatatableSequenceNumber;
import org.spout.api.math.MathHelper;

/**
 * Implements a store that stores {int, &lt;T&gt;} elementa.<br>
 * <br>
 * When an element is added, it is stored at an index decided by the store.<br>
 * <br>
 * The arrays used to store the elements are dynamically resized as more
 * elements are added.<br>
 * <br>
 * The number of elements added to the store may temporarily exceed the max
 * value, but only if a removal is performed shortly afterwards. The store will
 * spin lock until the number of elements is brought below the limit, so the
 * period of the violation should be short.<br>
 * <br>
 * A method is provided to test if an index is a reserved index, based on the
 * maximum lengths of the arrays. Only reserved indexes are used as element
 * indexes.<br>
 *
 * @param <T> the type of the Object in the {int, &lt;T&gt;} pair
 */
public final class AtomicIntArrayStore {

	private final int SPINS = 10;
	private final int MAX_FAIL_THRESHOLD = 256;
	private final int WAIT_COUNT = 32;
	private final int WAIT_MASK = WAIT_COUNT - 1;

	private final int maxLength;
	private final AtomicInteger length = new AtomicInteger(0);
	private final AtomicInteger entries = new AtomicInteger(0);
	private final AtomicInteger scan = new AtomicInteger(0);
	private final int reservedMask;

	private final AtomicReference<boolean[]> emptyArray;
	private final AtomicReference<AtomicIntegerArray> seqArray;
	private final AtomicReference<int[]> intArray;
	
	private AtomicInteger[] waiting;

	public AtomicIntArrayStore(int maxEntries) {
		this(maxEntries, 0.49);
	}

	public AtomicIntArrayStore(int maxEntries, double loadFactor) {
		this(maxEntries, loadFactor, 0);
	}

	public AtomicIntArrayStore(int maxEntries, double loadFactor, int initialSize) {
		this.maxLength = MathHelper.roundUpPow2((int) (maxEntries / loadFactor));
		this.reservedMask = -MathHelper.roundUpPow2(maxLength) & 0xFFFF;

		this.length.set(MathHelper.roundUpPow2(initialSize));
		this.entries.set(0);

		intArray = new AtomicReference<int[]>(new int[this.length.get()]);
		seqArray = new AtomicReference<AtomicIntegerArray>(new AtomicIntegerArray(this.length.get()));
		emptyArray = new AtomicReference<boolean[]>(new boolean[this.length.get()]);
		emptyFill(emptyArray.get(), seqArray.get());
		waiting = new AtomicInteger[WAIT_COUNT];
		for (int i = 0; i < WAIT_COUNT; i++) {
			waiting[i] = new AtomicInteger(0);
		}
	}

	/**
	 * Indicates if the given short should be reserved.<br>
	 * <br>
	 * Only ids where isReverved(id) returns true will be returned by the
	 * add(...) method.<br>
	 * <br>
	 * Ids from (65536 - length) to 65535 are reserved. <br>
	 * The top 2 bytes of the id are ignored.<br>
	 *
	 * @param id
	 * @return true if the id is reserved
	 */
	public final boolean isReserved(int id) {
		id = id & 0x0000FFFF;
		return (id & reservedMask) == reservedMask;
	}

	/**
	 * Gets the int value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is
	 * undefined.<br>
	 * <br>
	 *
	 * @param index the index
	 * @return the int value
	 */
	public final int getInt(int index) {
		index = toInternal(index);
		int spins = 0;
		boolean interrupted = false;
		while (true) {
			if (spins++ > SPINS) {
				interrupted |= atomicWait(index);
			}
			int initialSequence = seqArray.get().get(index);
			if (initialSequence == DatatableSequenceNumber.UNSTABLE) {
				continue;
			}
			int value = intArray.get()[index];
			if (!seqArray.get().compareAndSet(index, initialSequence, initialSequence)) {
				continue;
			}
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
			return value;
		}
	}

	/**
	 * Gets the sequence number associated with the element at a given index.<br>
	 * <br>
	 * A sequence number of DatatableSequenceNumber.UNSTABLE indicates that the
	 * record is unstable.<br>
	 * <br>
	 * This method should NOT be used to test if a sequence number has changed.
	 * Use testSequence(int index, int sequence) instead.
	 *
	 * @param index the index
	 * @return the sequence number
	 */
	public int getSequence(int index) {
		return this.seqArray.get().get(toInternal(index));
	}

	/**
	 * Tests if the sequence number has changed for a particular index.<br>
	 * <br>
	 * This method counts as both a volatile read and write, which is required
	 * for confirming that no change has occurred in another thread.
	 *
	 * @param index the index
	 * @param expected the expected sequence number
	 * @return true if the sequence number matches expected
	 */
	public boolean testSequence(int index, int expected) {
		return seqArray.get().compareAndSet(toInternal(index), expected, expected);
	}
	
	/**
	 * Tests if the location referred to by a particular index is stable
	 * 
	 * @param index
	 * @return true if stable
	 */
	public boolean testUnstable(int index) {
		return testUnstableInternal(toInternal(index));
	}
	
	private boolean testUnstableInternal(int index) {
		int expected = DatatableSequenceNumber.UNSTABLE;
		return seqArray.get().compareAndSet(index, expected, expected);
	}

	/**
	 * Gets the id value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is
	 * undefined.<br>
	 * <br>
	 *
	 * @param index the index
	 * @return the int value
	 */
	public final short getId(int index) {
		return (short) (getInt(index) >> 16);
	}

	/**
	 * Gets the data value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is
	 * undefined.<br>
	 * <br>
	 *
	 * @param index the index
	 * @return the int value
	 */
	public final short getData(int index) {
		return (short) getInt(index);
	}

	/**
	 * Adds an entry to the store. The auxData parameter should be set to null
	 * to indicate no auxiliary data.<br>
	 * <br>
	 * The index that is returned is guaranteed to be one of the reserved
	 * indexes.<br>
	 * <br>
	 *
	 * @param id the id
	 * @param data the data
	 * @param auxData the auxiliary data
	 * @return the index that the entry was stored in the array
	 */
	public final int add(short id, short data) {
		entries.incrementAndGet();

		while (true) {
			if (needsResize()) {
				resizeArrays();
			}
			int testIndex = scan.getAndIncrement() & length.get() - 1;
			int prevSeq = seqArray.get().getAndSet(testIndex, DatatableSequenceNumber.UNSTABLE);
			if (prevSeq == DatatableSequenceNumber.UNSTABLE) {
				continue;
			}
			try {
				if (!emptyArray.get()[testIndex]) {
					continue;
				}
				int idAndData = id << 16 | data & 0xFFFF;
				intArray.get()[testIndex] = idAndData;
				emptyArray.get()[testIndex] = false;
				return toExternal(testIndex);
			} finally {
				seqArray.get().set(testIndex, DatatableSequenceNumber.get());
				atomicNotify(testIndex);
			}
		}
	}

	/**
	 * Removes the array elements at the given index. The array should not be
	 * empty at the index in question.<br>
	 * <br>
	 *
	 * @param index the index
	 * @return the int for old entry, or 0 if none
	 */
	public int remove(int index) {
		index = toInternal(index);

		while (true) {
			int prevSeq = seqArray.get().getAndSet(index, DatatableSequenceNumber.UNSTABLE);
			if (prevSeq == DatatableSequenceNumber.UNSTABLE) {
				continue;
			}
			try {
				int oldInt = intArray.get()[index];
				boolean current = emptyArray.get()[index];
				if (current) {
					throw new IllegalStateException("Expected to remove a record but no record was found");
				}
				emptyArray.get()[index] = true;
				entries.decrementAndGet();
				return oldInt;
			} finally {
				seqArray.get().set(index, DatatableSequenceNumber.get());
				atomicNotify(index);
			}
		}
	}
	
	/**
	 * Attempts to lock the store.<br>
	 * <br>
	 * The lock will fail if the first element is already locked.  Once the first element is locked, it will keep attempting to lock the rest of the store until all elements are locked.
	 * <br>
	 * NOTE:  The store using spinning locks, so it must only be locked for a very short period of time
	 * 
	 * @return true if the store is locked
	 */
	public boolean tryLock() {
		return tryLock(-1);
	}
	
	/**
	 * Attempts to lock the store.<br>
	 * <br>
	 * The lock will fail if the first element is already locked.  <br>
	 * <br>
	 * Once the first element is successfully locked, it will keep attempting to lock the rest of the store until all elements are locked, or the number of times it fails to lock exceeds maxFails.
	 * <br>
	 * NOTE:  The store using spinning locks, so it must only be locked for a very short period of time
	 * 
	 * @param maxFails the maximum number of lock failures before the method returns false
	 * @return true if the store is locked
	 */
	public boolean tryLock(int maxFails) {
		int lockedIndexes = 0;
		// Lock the first element
		int firstSeq;
		firstSeq = seqArray.get().getAndSet(0, DatatableSequenceNumber.UNSTABLE);
		if (firstSeq == DatatableSequenceNumber.UNSTABLE) {
			return false;
		}
		lockedIndexes++;

		int fails = 0;
		
		// Lock the remaining elements
		for (int i = 1; i < length.get(); i++) {
			int seq;
			do {
				if (fails > maxFails && maxFails > 0) {
					unlock(lockedIndexes);
					return false;
				}

				seq = seqArray.get().getAndSet(i, DatatableSequenceNumber.UNSTABLE);
				if (seq == DatatableSequenceNumber.UNSTABLE) {
					fails++;
				}
			} while (seq == DatatableSequenceNumber.UNSTABLE);
			lockedIndexes++;
		}
		return true;
	}
	
	/**
	 * Unlocks the store.
	 */
	public void unlock() {
		unlock(length.get());
	}
	
	private void unlock(int lockedIndexes) {
		for (int i = 0; i < lockedIndexes; i++) {
			if (!seqArray.get().compareAndSet(i, DatatableSequenceNumber.UNSTABLE, DatatableSequenceNumber.get())) {
				throw new IllegalStateException("Element " + i + " + was not locked when released by unlock");
			}
			atomicNotify(i);
		}
	}

	/**
	 * Resizes the arrays, if required.
	 *
	 * The array length is doubled if needsResize returns true.
	 */
	private void resizeArrays() {
		boolean locked = false;
		while (needsResize() && !(locked = tryLock(MAX_FAIL_THRESHOLD)))
			;
		
		if (!locked) {
			return;
		}
		int lockedIndexes = length.get();
		try {
			// Calculate new length
			int newLength = length.get() << 1;
			if (newLength > maxLength || !needsResize()) {
				return;
			}

			//
			int[] newIntArray = new int[newLength];
			boolean[] newEmptyArray = new boolean[newLength];
			AtomicIntegerArray newSeqArray = new AtomicIntegerArray(newLength);
			emptyFill(newEmptyArray, null);

			// Copy the state of the current array to the new array
			for (int i = 0; i < length.get(); i++) {
				newIntArray[i] = intArray.get()[i];
				newEmptyArray[i] = emptyArray.get()[i];
				newSeqArray.set(i, DatatableSequenceNumber.UNSTABLE);
			}

			// Set the top half of the new array to unstable and EMPTY
			for (int i = length.get(); i < newLength; i++) {
				newSeqArray.set(i, DatatableSequenceNumber.UNSTABLE);
				newEmptyArray[i] = true;
			}
			intArray.set(newIntArray);
			emptyArray.set(newEmptyArray);
			seqArray.set(newSeqArray);

			int oldLength = length.get();

			// Update the length, the array already has been lengthened, so this is safe
			length.set(newLength);

			// Set the top half of the array's sequence number to 0 (from UNSTABLE)
			for (int i = oldLength; i < newLength; i++) {
				if (!seqArray.get().compareAndSet(i, DatatableSequenceNumber.UNSTABLE, DatatableSequenceNumber.get())) {
					throw new IllegalStateException("Element " + i + " + was not locked when released during resizing");
				}
			}
		} finally {
			unlock(lockedIndexes);
		}

	}

	/**
	 * Converts an internal index to an external index.
	 *
	 * @param internal the internal index
	 * @return the equivalent external index
	 */
	private final int toExternal(int internal) {
		return (internal | reservedMask) & 0xFFFF;
	}

	/**
	 * Converts an external index to an internal index.
	 *
	 * @param external the external index
	 * @return the equivalent internal index
	 */
	private final int toInternal(int external) {
		return external & ~reservedMask & 0xFFFF;
	}

	/**
	 * Fills an auxiliary array with all empty objects.
	 *
	 * @param array the array to fill
	 */
	private final void emptyFill(boolean[] array, AtomicIntegerArray iArray) {
		for (int i = 0; i < array.length; i++) {
			array[i] = true;
			if (iArray != null) {
				iArray.set(i, DatatableSequenceNumber.get());
			}
		}
	}

	/**
	 * Gets the size of the internal arrays
	 *
	 * @return the size of the arrays
	 */
	public final int getSize() {
		return length.get();
	}

	/**
	 * Gets the number of entries in the store
	 *
	 * @return the size of the arrays
	 */
	public final int getEntries() {
		return entries.get();
	}

	/**
	 * Indicates if the array needs resizing. An array is considered to need
	 * resizing if it is more than 50% full.
	 *
	 * Once an array has a length of the maximum length, it is never considered
	 * in need to resizing.
	 *
	 * @return true if the array needs to be resized
	 */
	private final boolean needsResize() {
		int lengthThreshold = length.get();
		lengthThreshold -= lengthThreshold >> 2;
		return length.get() < maxLength && entries.get() >= lengthThreshold;
	}

	/**
	 * Waits until a notify
	 *
	 * @return true if interrupted during the wait
	 */
	private final boolean atomicWait(int index) {
		AtomicInteger i = getWaitingInternal(index);
		i.incrementAndGet();
		try {
			synchronized (i) {
				if (!testUnstableInternal(index)) {
					return false;
				}
				try {
					i.wait();
				} catch (InterruptedException e) {
					return true;
				}
			}
		} finally {
			i.decrementAndGet();
		}
		return false;
	}

	/**
	 * Notifies all waiting threads
	 */
	private final void atomicNotify(int index) {
		AtomicInteger i = getWaitingInternal(index);
		if (!i.compareAndSet(0, 0)) {
			synchronized (i) {
				i.notifyAll();
			}
		}
	}
	
	/**
	 * Gets the waiting counter for the given index
	 * 
	 * @param index
	 * @return
	 */
	public final AtomicInteger getWaiting(int index) {
		return getWaitingInternal(toInternal(index));
	}
	
	private final AtomicInteger getWaitingInternal(int index) {
		return waiting[index & WAIT_MASK];
	}
}
