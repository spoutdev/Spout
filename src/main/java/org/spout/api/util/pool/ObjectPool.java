package org.spout.api.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ObjectPool <T extends PoolableObject>{
	
	Queue<T> pool = new ConcurrentLinkedQueue<T>();
	
	public T getInstance(){
		if(pool.isEmpty()) return createNew();
		return pool.poll();
	}
	
	
	protected abstract T createNew();

	public void reclaim(T object){
		if(pool.contains(object)) throw new IllegalArgumentException("Cannot reclaim something that the pool already contains");
		pool.add(object);
	}

}
