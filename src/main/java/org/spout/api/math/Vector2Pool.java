package org.spout.api.math;

import org.spout.api.util.pool.ObjectPool;

public class Vector2Pool extends ObjectPool<Vector2> {
	static Vector2Pool instance = new Vector2Pool();
	
	@Override
	protected Vector2 createNew() {
		return new Vector2();
	}
	
	public static Vector2 checkout(){
		return instance.getInstance();
	}
	
	public static void free(Vector2 obj){
		instance.reclaim(obj);
	}

}
