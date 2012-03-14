package org.spout.api.util.pool;

public abstract class PoolableObject {
	
	
	boolean isPooled;
	ObjectPool parentPool;
	
	
	public void pool(ObjectPool p){
		if(isPooled) throw new IllegalArgumentException("Object already pooled! Cannot pool again");
		isPooled = true;
		parentPool = p;
	}
	
	public void reclaim(){
		parentPool.reclaim(this);
	}
	
	@Override
	public void finalize(){
		if(this.isPooled) parentPool.reclaim(this);
		
	}

}
