package org.spout.api.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public abstract class ObjectPool <T extends PoolableObject>{
	
	Queue<T> pool = new ConcurrentLinkedQueue<T>();
	ConcurrentMap<T, Boolean> map = new ConcurrentHashMap<T, Boolean>();
	
	public T getInstance(){
		if(pool.isEmpty()) {
			T t = createNew();
			t.pool(this);
			return t;
		}
		T obj = pool.poll();
		map.remove(obj);
		return obj;
	}
	
	
	protected abstract T createNew();

	public void reclaim(T object){
		if(map.containsKey(object)) throw new IllegalArgumentException("Cannot reclaim something that the pool already contains");
		pool.add(object);
		map.put(object, Boolean.FALSE);
	}

}
