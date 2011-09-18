package org.getspout.spout.block.mcblock;

import net.minecraft.server.Block;

public interface CustomMCBlock {
	
	public Block getParent();
	
	public void setHardness(float hardness);

}
