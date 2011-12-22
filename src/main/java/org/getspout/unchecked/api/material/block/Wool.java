package org.getspout.unchecked.api.material.block;

import org.getspout.unchecked.api.material.SolidBlock;

public class Wool extends GenericBlockMaterial implements SolidBlock {

	public Wool(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean isFallingBlock() {
		return false;
	}

}
