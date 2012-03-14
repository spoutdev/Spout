package org.spout.api.util.pool;

public abstract class PoolableObject {
	
	
	boolean isPooled;
	ObjectPool parentPool;
	
	
	protected void pool(ObjectPool p){
		if(isPooled) throw new IllegalArgumentException("Object already pooled! Cannot pool again");
		isPooled = true;
		parentPool = p;
	}
	
	public void free(){
		parentPool.reclaim(this);
	}
	
	@Override
	public void finalize(){
		if(this.isPooled) free();
		
	}

}
