package org.getspout.commons.material;

import org.getspout.commons.material.Block;

public interface Plant extends Block{
	
	public boolean isHasGrowthStages();
	
	public int getNumGrowthStages();
		
	public int getMinimumLightToGrow();

}
