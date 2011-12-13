package org.getspout.commons.material;

import org.getspout.commons.material.BlockMaterial;

public interface Plant extends BlockMaterial{
	
	public boolean isHasGrowthStages();
	
	public int getNumGrowthStages();
		
	public int getMinimumLightToGrow();

}
