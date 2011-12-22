package org.getspout.unchecked.api.material.block;

import org.getspout.unchecked.api.material.BlockMaterial;

public class Air extends GenericBlockMaterial implements BlockMaterial {

	public Air(String name) {
		super(name, 0);
	}

	@Override
	public float getFriction() {
		return 0;
	}

	@Override
	public BlockMaterial setFriction(float slip) {
		return this;
	}

	@Override
	public float getHardness() {
		return 0;
	}

	@Override
	public BlockMaterial setHardness(float hardness) {
		return this;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public BlockMaterial setOpaque(boolean opaque) {
		return this;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public BlockMaterial setLightLevel(int level) {
		return this;
	}

}
