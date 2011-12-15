package org.getspout.api.material.block;

import org.getspout.api.material.Plant;
import org.getspout.api.material.block.GenericBlockMaterial;

public class Grass extends GenericBlockMaterial implements Plant{
	
	public Grass(String name){
		super(name, 2);
	}

	public boolean isHasGrowthStages() {
		return false;
	}

	public int getNumGrowthStages() {
		return 0;
	}

	public int getMinimumLightToGrow() {
		return 9;
	}

}
