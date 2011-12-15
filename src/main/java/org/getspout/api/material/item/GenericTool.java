package org.getspout.api.material.item;

import org.getspout.api.material.BlockMaterial;
import org.getspout.api.material.Tool;
import org.getspout.api.material.item.GenericItemMaterial;

public class GenericTool extends GenericItemMaterial implements Tool {

	public GenericTool(String name, int id) {
		super(name, id);
	}

	public short getDurability() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Tool setDurability(short durability) {
		// TODO Auto-generated method stub
		return this;
	}

	public float getStrengthModifier(BlockMaterial block) {
		// TODO Auto-generated method stub
		return 0;
	}
		
	public Tool setStrengthModifier(BlockMaterial block, float modifier) {
		// TODO Auto-generated method stub
		return this;
	}

	public BlockMaterial[] getStrengthModifiedBlocks() {
		// TODO Auto-generated method stub
		return null;
	}

}
