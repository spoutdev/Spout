package org.getspout.api.material.block;

import org.getspout.api.material.Plant;

public class LongGrass extends GenericBlockMaterial implements Plant {

	public LongGrass(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean isHasGrowthStages() {
		return false;
	}

	public int getNumGrowthStages() {
		return 0;
	}

	public int getMinimumLightToGrow() {
		return 0;
	}

}
