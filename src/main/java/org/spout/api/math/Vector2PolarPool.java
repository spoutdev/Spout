package org.spout.api.math;

import org.spout.api.util.pool.ObjectPool;

public class Vector2PolarPool extends ObjectPool<Vector2Polar> {
	static Vector2PolarPool instance = new Vector2PolarPool();
	
	@Override
	protected Vector2Polar createNew() {
		return new Vector2Polar();
	}
	
	public static Vector2Polar checkout(){
		return instance.getInstance();
	}
	
	public static void free(Vector2Polar obj){
		instance.reclaim(obj);
	}

}
