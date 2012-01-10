package org.spout.api.basic.blocks;

import org.spout.api.material.GenericBlockMaterial;

public final class SpoutBlock extends GenericBlockMaterial {

	float friction = 0.8f;
	float hardness = 1.f;

	public SpoutBlock(String name, int id) {
		super(name, id);
		this.setFriction(0.8f).setHardness(1.0F).setLightLevel(15);
	}
}
