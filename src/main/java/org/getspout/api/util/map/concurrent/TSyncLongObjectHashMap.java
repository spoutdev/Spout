package org.getspout.api.util.map.concurrent;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TSyncLongObjectHashMap<V> implements TLongObjectMap<V> {
	
	private final int mapCount;
	private final ReadWriteLock[] lockArray;
	private final TLongObjectHashMap<V>[] mapArray;
	private final long no_entry_key;
	private final AtomicInteger totalKeys = new AtomicInteger(0);

	public TSyncLongObjectHashMap() {
		this(16);
	}
	
	public TSyncLongObjectHashMap(int mapCount) {
		this(mapCount, 32);
	}
	
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity) {
		this(mapCount, initialCapacity, 0.5F);
	}
	
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity, float loadFactor) {
		this(mapCount, initialCapacity, loadFactor, Constants.DEFAULT_LONG_NO_ENTRY_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity, float loadFactor, long noEntryKey) {
		this.mapCount = mapCount;
		mapArray = new TLongObjectHashMap[mapCount];
		lockArray = new ReadWriteLock[mapCount];
		for (int i = 0; i < mapCount; i++) {
			mapArray[i] = new TLongObjectHashMap<V>(initialCapacity / mapCount, loadFactor, noEntryKey);
			lockArray[i] = new ReentrantReadWriteLock();
		}
		this.no_entry_key = noEntryKey;
	}
	
	public void clear() {
		for (int m = 0; m < mapCount; m++) {
			clear(m);
		}
	}
	
	private void clear (int m) {
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			totalKeys.addAndGet(-mapArray[m].size());
			mapArray[m].clear();
		} finally {
			lock.unlock();
		}
	}

	public boolean containsKey(long key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	public boolean containsValue(Object value) {
		for (int m = 0; m < mapCount; m++) {
			if (containsValue(m, value)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsValue(int m, Object value) {
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsValue(value);
		} finally {
			lock.unlock();
		}
	}

	public boolean forEachEntry(TLongObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public boolean forEachKey(TLongProcedure arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public boolean forEachValue(TObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public V get(long key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].get(key);
		} finally {
			lock.unlock();
		}
	}

	public long getNoEntryKey() {
		return no_entry_key;
	}

	public boolean isEmpty() {
		return totalKeys.get() == 0;
	}

	public TLongObjectIterator<V> iterator() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public TLongSet keySet() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public long[] keys(long[] dest) {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			long[] keys;
			if (dest == null || dest.length < localSize) {
				keys = new long[localSize];
			} else {
				keys = dest;
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				long[] mapKeys = mapArray[m].keys();
				for (int mapPosition = 0; mapPosition < mapKeys.length; mapPosition++) {
					keys[position++] = mapKeys[mapPosition];
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			return keys;
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}

	public long[] keys() {
		return keys(null);
	}

	public V put(long key, V value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].put(key, value);
			if (previous == null && value != null) {
				totalKeys.incrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}
	
	// TODO - these should be implemented
	public void putAll(Map<? extends Long, ? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public void putAll(TLongObjectMap<? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public V putIfAbsent(long key, V value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].putIfAbsent(key, value);
			if (previous == null && value != null) {
				totalKeys.incrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}

	public V remove(long key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].remove(key);
			if (previous != null) {
				totalKeys.decrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}

	public boolean retainEntries(TLongObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public int size() {
		return totalKeys.get();
	}

	public void transformValues(TObjectFunction<V, V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");		
	}

	public Collection<V> valueCollection() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public V[] values() {
		return values(null);
	}

	@SuppressWarnings("unchecked")
	public V[] values(V[] dest) {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			V[] values;
			if (dest == null || dest.length < localSize) {
				values = (V[])new Object[size()];
			} else {
				values = dest;
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				V[] mapValues = mapArray[m].values();
				for (int mapPosition = 0; mapPosition < mapValues.length; mapPosition++) {
					values[position++] = mapValues[mapPosition];
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			return values;
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}
	
	private int mapHash(long key) {
		int intKey = 0x7FFFFFFF & (int)(key + (key << 21));
		return intKey % mapCount;
	}

}
