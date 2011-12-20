package org.getspout.api.material;

public interface Plant extends BlockMaterial {

	public boolean isHasGrowthStages();

	public int getNumGrowthStages();

	public int getMinimumLightToGrow();

}
