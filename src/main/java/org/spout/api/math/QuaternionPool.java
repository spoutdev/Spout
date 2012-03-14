package org.spout.api.math;

import org.spout.api.util.pool.ObjectPool;

public class QuaternionPool extends ObjectPool<Quaternion> {
	static QuaternionPool instance = new QuaternionPool();
	
	@Override
	protected Quaternion createNew() {
		return new Quaternion();
	}
	
	public static Quaternion checkout(){
		return instance.getInstance();
	}
	
	public static void free(Quaternion obj){
		instance.reclaim(obj);
	}

}
