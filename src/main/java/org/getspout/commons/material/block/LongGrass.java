package org.getspout.commons.material.block;

import org.getspout.commons.material.Plant;
import org.getspout.commons.material.block.GenericBlockMaterial;

public class LongGrass extends GenericBlockMaterial implements Plant{

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
