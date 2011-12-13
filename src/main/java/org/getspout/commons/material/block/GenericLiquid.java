package org.getspout.commons.material.block;

import org.getspout.commons.material.Liquid;
import org.getspout.commons.material.block.GenericBlockMaterial;

public class GenericLiquid extends GenericBlockMaterial implements Liquid{
	private final boolean flowing;
	public GenericLiquid(String name, int id, boolean flowing) {
		super(name, id);
		this.flowing = flowing;
	}

	public boolean isFlowing() {
		return flowing;
	}

}
