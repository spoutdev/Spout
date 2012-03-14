package org.spout.api.math;

import org.spout.api.util.pool.ObjectPool;

public class Vector4Pool extends ObjectPool<Vector4> {
	static Vector4Pool instance = new Vector4Pool();
	
	@Override
	protected Vector4 createNew() {		
		return new Vector4();
	}
	
	public static Vector4 checkout(){
		return instance.getInstance();
	}
	
	public static void free(Vector4 obj){
		instance.reclaim(obj);
	}

}
