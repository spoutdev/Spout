package org.getspout.commons.material;

import org.getspout.commons.material.Block;
import org.getspout.commons.material.Item;
import org.getspout.commons.material.Tool;

public interface Tool extends Item {
	
	public short getDurability();
	
	public Tool setDurability(short durability);

	public float getStrengthModifier(Block block);
	
	public Tool setStrengthModifier(Block block, float modifier);
	
	public Block[] getStrengthModifiedBlocks();
}
