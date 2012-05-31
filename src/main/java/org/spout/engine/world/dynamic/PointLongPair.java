package org.spout.engine.world.dynamic;


public class PointLongPair extends PointAlone {
	
	private static final long serialVersionUID = 1L;
	
	private final long updateTime;
	
	public PointLongPair(int x, int y, int z, long updateTime) {
		super(null, x, y, z);
		this.updateTime = updateTime;
	}
	
	public long getUpdateTime() {
		return updateTime;
	}

}
