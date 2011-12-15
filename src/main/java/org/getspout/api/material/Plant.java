package org.getspout.api.material;

import org.getspout.api.material.BlockMaterial;

public interface Plant extends BlockMaterial{
	
	public boolean isHasGrowthStages();
	
	public int getNumGrowthStages();
		
	public int getMinimumLightToGrow();

}
