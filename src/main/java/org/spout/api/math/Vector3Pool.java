package org.spout.api.math;

import org.spout.api.util.pool.ObjectPool;

public class Vector3Pool extends ObjectPool<Vector3> {

	static Vector3Pool instance = new Vector3Pool();
	
	@Override
	protected Vector3 createNew() {
		return new Vector3();
	
	}
	
	public static Vector3 checkout(){
		return instance.getInstance();
	}
	
	public static void free(Vector3 obj){
		instance.reclaim(obj);
	}
	
	
	
	

}
