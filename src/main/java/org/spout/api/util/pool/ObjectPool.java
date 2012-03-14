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
package org.spout.api.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ObjectPool<T extends PoolableObject> {

	Queue<T> pool = new ConcurrentLinkedQueue<T>();

	/**
	 * Returns an object out of the pool.  
	 * If the pool is exhausted, then a new object will be created.
	 * 
	 * @return
	 */
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

	/**
	 * Returns an object into the pool.
	 * 
	 * @param object
	 */
	public void reclaim(T object) {
		if (!object.isFreed) {
			throw new IllegalArgumentException("Cannot reclaim something that the pool already contains");
		}
		if(object.parentPool != this){
			throw new IllegalArgumentException("Cannot reclaim something that isn't owned by this pool");
		}
		object.isFreed = true;
		pool.add(object);
	}
}
