package org.spout.api.util.pool;

import org.spout.api.math.Vector3;

public class Vector3Pool extends ObjectPool<Vector3> {

	static Vector3Pool instance = new Vector3Pool();
	
	@Override
	protected Vector3 createNew() {
		Vector3 v = Vector3.createRaw();
		v.pool(this);
		return v;
	}
	
	public static Vector3 checkout(){
		return instance.getInstance();
	}
	
	public static void free(Vector3 obj){
		instance.reclaim(obj);
	}
	
	
	
	

}
