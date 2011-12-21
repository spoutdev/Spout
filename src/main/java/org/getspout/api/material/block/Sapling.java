package org.getspout.api.material.block;

import org.getspout.api.material.Plant;

public class Sapling extends GenericBlockMaterial implements Plant {

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
