package org.getspout.api.material.block;

import org.getspout.api.material.Liquid;

public class GenericLiquid extends GenericBlockMaterial implements Liquid {
	private final boolean flowing;

	public GenericLiquid(String name, int id, boolean flowing) {
		super(name, id);
		this.flowing = flowing;
	}

	public boolean isFlowing() {
		return flowing;
	}

}
