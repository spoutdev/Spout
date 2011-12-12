package org.getspout.commons.material.block;

import org.getspout.commons.material.SolidBlock;
import org.getspout.commons.material.block.GenericBlock;

public class Wool extends GenericBlock implements SolidBlock {

	public Wool(String name, int id, int data) {
		super(name, id, data);
	}

	public boolean isFallingBlock() {
		return false;
	}

}
