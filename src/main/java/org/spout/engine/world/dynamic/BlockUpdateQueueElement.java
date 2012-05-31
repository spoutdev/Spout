package org.spout.engine.world.dynamic;

public interface BlockUpdateQueueElement {
	
	public int getBlockX();
	public int getBlockY();
	public int getBlockZ();
	public long getUpdateTime();
	public Object getHint();

}
