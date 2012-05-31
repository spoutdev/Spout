package org.spout.engine.world.dynamic;

import org.spout.api.geo.discrete.Point;

public class PointLongObjectTriplet extends PointLongPair {
	
	private static final long serialVersionUID = 1L;
	
	private final Object o;
	
	public PointLongObjectTriplet(int x, int y, int z, long updateTime, Object o) {
		super(x, y, z, updateTime);
		this.o = o;
	}
	
	public Object getHint() {
		return o;
	}

}
