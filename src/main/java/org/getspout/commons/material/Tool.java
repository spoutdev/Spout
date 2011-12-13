package org.getspout.commons.material;

import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.ItemMaterial;
import org.getspout.commons.material.Tool;

public interface Tool extends ItemMaterial {
	
	public short getDurability();
	
	public Tool setDurability(short durability);

	public float getStrengthModifier(BlockMaterial block);
	
	public Tool setStrengthModifier(BlockMaterial block, float modifier);
	
	public BlockMaterial[] getStrengthModifiedBlocks();
}
