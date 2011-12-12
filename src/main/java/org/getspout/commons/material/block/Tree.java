package org.getspout.commons.material.block;

import org.getspout.commons.material.Plant;
import org.getspout.commons.material.block.GenericBlock;

public class Tree extends GenericBlock implements Plant{

	public Tree(String name, int id, int data) {
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
