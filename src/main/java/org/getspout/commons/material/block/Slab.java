package org.getspout.commons.material.block;

import org.getspout.commons.material.SolidBlock;
import org.getspout.commons.material.block.GenericBlockMaterial;

public class Slab extends GenericBlockMaterial implements SolidBlock{

	public Slab(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean isFallingBlock() {
		return false;
	}

}
