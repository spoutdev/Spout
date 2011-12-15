package org.getspout.api.material.block;

import org.getspout.api.material.SolidBlock;
import org.getspout.api.material.block.GenericBlockMaterial;

public class Wool extends GenericBlockMaterial implements SolidBlock {

	public Wool(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean isFallingBlock() {
		return false;
	}

}
