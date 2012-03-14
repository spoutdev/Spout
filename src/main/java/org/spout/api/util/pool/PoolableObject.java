package org.spout.api.util.pool;

public abstract class PoolableObject {

	boolean isPooled;
	boolean isFreed;
	ObjectPool parentPool;

	protected void pool(ObjectPool p) {
		if (isPooled) {
			throw new IllegalArgumentException("Object already pooled! Cannot pool again");
		}
		isPooled = true;
		isFreed = false;
		parentPool = p;
	}

	public void free() {
		isFreed = true;
		parentPool.reclaim(this);
	}

	@Override
	public void finalize() {
		if (isPooled) {
			free();
		}
	}
}
