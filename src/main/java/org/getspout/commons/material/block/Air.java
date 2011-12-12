package org.getspout.commons.material.block;

import org.getspout.commons.material.Block;
import org.getspout.commons.material.block.GenericBlock;

public class Air extends GenericBlock implements Block {

	public Air(String name) {
		super(name, 0);
	}

	@Override
	public float getFriction() {
		return 0;
	}

	@Override
	public Block setFriction(float slip) {
		return this;
	}

	@Override
	public float getHardness() {
		return 0;
	}

	@Override
	public Block setHardness(float hardness) {
		return this;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public Block setOpaque(boolean opaque) {
		return this;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public Block setLightLevel(int level) {
		return this;
	}

}
