package org.spout.api.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ObjectPool<T extends PoolableObject> {

	Queue<T> pool = new ConcurrentLinkedQueue<T>();

	public T getInstance() {
		if (pool.isEmpty()) {
			T t = createNew();
			t.pool(this);
			return t;
		}
		T obj = pool.poll();
		obj.isFreed = false;
		return obj;
	}

	protected abstract T createNew();

	public void reclaim(T object) {
		if (object.isFreed) {
			throw new IllegalArgumentException("Cannot reclaim something that the pool already contains");
		}
		pool.add(object);
	}
}
