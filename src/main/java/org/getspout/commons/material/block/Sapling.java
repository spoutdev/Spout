package org.getspout.commons.material.block;

import org.getspout.commons.material.Plant;
import org.getspout.commons.material.block.GenericBlockMaterial;

public class Sapling extends GenericBlockMaterial implements Plant{

	public Sapling(String name, int data) {
		super(name, 6, data);
	}

	public boolean isHasGrowthStages() {
		return true;
	}

	public int getNumGrowthStages() {
		return 3;
	}

	public int getMinimumLightToGrow() {
		return 8;
	}

}
