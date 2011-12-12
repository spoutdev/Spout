package org.getspout.commons.material;

import org.getspout.commons.material.Block;
import org.getspout.commons.material.Material;

public interface Block extends Material{
	
	public float getFriction();
	
	public Block setFriction(float slip);
	
	public float getHardness();
	
	public Block setHardness(float hardness);
	
	public boolean isOpaque();
	
	public Block setOpaque(boolean opaque);
	
	public int getLightLevel();
	
	public Block setLightLevel(int level);
}
