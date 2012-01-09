package org.spout.api.util.map.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.math.MathHelper;

/**
 * Implements a store that stores {int, &lt;T&gt;} elementa.<br>
 * <br>
 * When an element is added, it is stored at an index decided by the store.<br>
 * <br>
 * The arrays used to store the elements are dynamically resized as more elements are added.<br>
 * <br>
 * The number of elements added to the store may temporarily exceed the max value, but only if a removal is performed shortly afterwards.  The store will spin lock until the number of elements is brought below the limit, so the period of the violation should be short.<br>
 * <br>
 * A method is provided to test if an index is a reserved index, based on the maximum lengths of the arrays.  Only reserved indexes are used as element indexes.<br>
 * @param <T> the type of the Object in the {int, &lt;T&gt;} pair
 */
public final class AtomicIntReferenceArrayStore<T> {

	@SuppressWarnings("unchecked")
	public final T EMPTY = (T)new Object();

	private final int maxLength;
	private AtomicInteger length = new AtomicInteger(0);
	private AtomicInteger entries = new AtomicInteger(0);
	private AtomicInteger scan = new AtomicInteger(0);
	private final int reservedMask;

	/**
	 * This read/write lock is used to handle locking for resizing operations.<br>
	 * <br>
	 * All operations on the store (including add/remove operations) obtain a read lock before performing the operation.<br>
	 * <br>
	 * Since an unlimited number of threads can hold read locks at the same time, this has no performance impact.<br>
	 * <br>
	 * When performing a resize operation, a write lock is obtained.  This ensures exclusive access to the arrays in order to perform the resize.
	 */
	private final ReadWriteLock resizeLock = new ReentrantReadWriteLock();

	private AtomicIntegerArray intArray;
	private AtomicReferenceArray<T> auxArray;

	public AtomicIntReferenceArrayStore(int maxEntries, double loadFactor, int initialSize) {
		this.maxLength = MathHelper.roundUpPow2((maxEntries * 3) / 2); // ~50% load factor
		this.reservedMask = (-MathHelper.roundUpPow2(maxLength)) & 0xFFFF;

		this.length.set(MathHelper.roundUpPow2(initialSize));
		this.entries.set(0);

		intArray = new AtomicIntegerArray(this.length.get());
		auxArray = new AtomicReferenceArray<T>(this.length.get());		
		emptyFill(auxArray);
	}

	/**
	 * Indicates if the given short should be reserved.<br>
	 * <br>
	 * Only ids where isReverved(id) returns true will be returned by the add(...) method.<br>
	 * <br>
	 * Ids from (65536 - length) to 65535 are reserved. 
	 * <br>
	 * @param id
	 * @return true if the id is reserved
	 */
	public final boolean isReserved(int id) {
		return ((id & 0xFFFF0000) == 0) && ((id & reservedMask) == id);
	}

	/**
	 * Gets the int value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is undefined.<br>
	 * <br>
	 * @param index the index
	 * @return the int value
	 */
	public final int getInt(int index) {
		Lock lock = obtainReadLockForGet();
		try {
			return intArray.get(toInternal(index));
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Gets the id value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is undefined.<br>
	 * <br>
	 * @param index the index
	 * @return the int value
	 */
	public final short getId(int index) {
		return (short)(getInt(index) >> 16);
	}
	
	/**
	 * Gets the data value stored at a given index.<br>
	 * <br>
	 * If there is no int stored at the index, then the return value is undefined.<br>
	 * <br>
	 * @param index the index
	 * @return the int value
	 */
	public final short getData(int index) {
		return (short)(getInt(index));
	}

	/**
	 * Gets the auxiliary data at a given index.<br>
	 * <br>
	 * If there is no data (int or auxiliary data) stored at the index, then the return value is the EMPTY object.<br>
	 * <br>
	 * If there is no auxiliary data, but there is int data, then the method returns null.<br>
	 * <br>
	 * This EMPTY object is NOT a valid &lt;T&gt; object.<br>
	 * <br>
	 * @param index the index
	 * @return the auxiliary data object, null, or EMPTY
	 */
	public final T getAuxData(int index) {
		Lock lock = obtainReadLockForGet();
		try {
			return auxArray.get(toInternal(index));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Adds an entry to the store.  The auxData parameter should be set to null to indicate no auxiliary data.<br>
	 * <br>
	 * The index that is returned is guaranteed to be one of the reserved indexes.<br>
	 * <br>
	 * @param id the id
	 * @param data the data
	 * @param auxData the auxiliary data
	 * @return the index that the entry was stored in the array
	 */
	public final int add(short id, short data, T auxData) {
		if (auxData == EMPTY) {
			throw new IllegalArgumentException("The EMPTY singleton may not be passed as auxilary data");
		}
		entries.incrementAndGet();

		while (true) {
			Lock lock = obtainReadLockForAdd();
			try {
				int testIndex = scan.getAndIncrement() & (length.get() - 1);
				boolean success = auxArray.compareAndSet(testIndex, EMPTY, auxData);
				if (success) {
					int idAndData = (((int)id) << 16) | (data & 0xFFFF);
					intArray.set(testIndex, idAndData);
					return toExternal(testIndex);
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * Removes the array elements at the given index.  The array should not be empty at the index in question.<br>
	 * <br>
	 * @param index the index
	 */
	public final void remove(int index) {
		int localIndex = toInternal(index);
		Lock lock = obtainReadLockForRemove();
		try {
			boolean success = false;
			T current = auxArray.get(localIndex);
			if (current == EMPTY) {
				throw new IllegalStateException("An attempt was made to remove from an empty ");
			}
			success = auxArray.compareAndSet(localIndex, current, EMPTY);
			if (success) {
				entries.decrementAndGet();
			} else {
				throw new IllegalStateException("An attempt was made to remove from an empty ");
			}
		} finally {
			lock.unlock();
		}
	}

	private final Lock obtainReadLockForRemove() {
		return obtainReadLock();
	}

	private final Lock obtainReadLockForGet() {
		return obtainReadLock();
	}
		
	/**
	 * Gets a locked read lock for performing get operations. <br>
	 * <br>
	 * This method implements a spinlock if the number of entries exceeds the threshold for a resize.<br>
	 * This prevents the store from being read locked if a resize is required.<br>
	 * All operations that had already obtained the read lock will eventually complete.<br>
	 * The add operation that caused the resize to be required (or another one) will actually perform the resize.<br>
	 * <br>
	 * @return the read lock (locked)
	 */
	private final Lock obtainReadLock() {
		while (needsResize()) {
		}
		Lock lock = resizeLock.readLock();
		lock.lock();
		return lock;
	}

	/**
	 * Gets a locked read lock for performing add operations.<br>
	 * <br>
	 * If a resize is required, an attempt will be made to write lock the store and if successful, the store will be resized.<br>
	 * <br>
	 * Otherwise, an attempt will be made to obtain a read lock.<br>
	 * <br>
	 * The method loops until a read lock is successfully obtained.<br>
	 * <br>
	 * While a resize is required, no new operations can start.  Once all the outstanding operations complete, the write lock can be obtained.  This prevents starvation.<br>
	 * <br>
	 * @return the read lock (locked)
	 */
	private final Lock obtainReadLockForAdd() {
		while (true) {
			while (!needsResize()) {
				Lock readLock = resizeLock.readLock();
				boolean success = readLock.tryLock();
				if (success) {
					return readLock;
				}
			}
			Lock writeLock = resizeLock.writeLock();
			boolean success = writeLock.tryLock();
			if (!success) {
				continue;
			} else {
				try {
					resizeArrays();
				} finally {
					writeLock.unlock();
				}
			}
		}
	}

	/**
	 * Resizes the arrays, if required.
	 * 
	 * The array length is doubled if needsResize returns true.
	 */
	private void resizeArrays() {
		int newLength = length.get() << 1;
		if (newLength > maxLength || !needsResize()) {
			return;
		}
		AtomicIntegerArray newIntArray = new AtomicIntegerArray(newLength);
		AtomicReferenceArray<T> newAuxArray = new AtomicReferenceArray<T>(newLength);
		emptyFill(newAuxArray);
		for (int i = 0; i < length.get(); i++) {
			newIntArray.set(i, intArray.get(i));
			newAuxArray.set(i, auxArray.get(i));
		}
		intArray = newIntArray;
		auxArray = newAuxArray;
		length.set(newLength);
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
		return (external | (~reservedMask)) & 0xFFFF;
	}
	
	/**
	 * Fills an auxiliary array with all empty objects.
	 * 
	 * @param array the array to fill
	 */
	private final void emptyFill(AtomicReferenceArray<T> array) {
		for (int i = 0; i < array.length(); i++) {
			array.set(i, EMPTY);
		}
	}

	/**
	 * Indicates if the array needs resizing.  An array is considered to need resizing if it is more than 50% full.
	 * 
	 * Once an array has a length of the maximum length, it is never considered in need to resizing.
	 * 
	 * @return true if the array needs to be resized
	 */
	// TODO - add timer that allows for resize downwards
	//      - store last time the array was > half of the threshold.  
	//      - if more than a threshold downsize the array
	private final boolean needsResize() {
		return length.get() != maxLength && entries.get() >= (length.get() >> 1);
	}
}
