package org.getspout.commons.material.block;

import org.getspout.commons.material.Liquid;
import org.getspout.commons.material.block.GenericBlock;

public class GenericLiquid extends GenericBlock implements Liquid{
	private final boolean flowing;
	public GenericLiquid(String name, int id, boolean flowing) {
		super(name, id);
		this.flowing = flowing;
	}

	public boolean isFlowing() {
		return flowing;
	}

}
