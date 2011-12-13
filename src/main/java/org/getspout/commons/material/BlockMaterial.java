package org.getspout.commons.material;

import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.Material;

public interface BlockMaterial extends Material{
	
	public float getFriction();
	
	public BlockMaterial setFriction(float slip);
	
	public float getHardness();
	
	public BlockMaterial setHardness(float hardness);
	
	public boolean isOpaque();
	
	public BlockMaterial setOpaque(boolean opaque);
	
	public int getLightLevel();
	
	public BlockMaterial setLightLevel(int level);
}
