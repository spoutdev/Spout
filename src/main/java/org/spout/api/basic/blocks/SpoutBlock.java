package org.spout.api.basic.blocks;

import org.spout.api.material.BlockMaterial;

public final class SpoutBlock implements BlockMaterial {
	final short id;
	String name;
	
	float friction = 0.8f;
	float hardness = 1.f;
	
	public SpoutBlock(String name, int id){
		this.id = (short)id;
		this.name = name;
	}

	public short getId() {
		return this.id;
	}

	public short getData() {
		return 0;
	}

	public boolean hasSubtypes() {
		return false;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return name;
	}

	public void setDisplayName(String name) {
		this.name = name;

	}

	public float getFriction() {
		return friction;
	}

	public BlockMaterial setFriction(float slip) {
		this.friction = slip;
		return this;
	}

	public float getHardness() {
	
		return hardness;
	}

	public BlockMaterial setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	public boolean isOpaque() {
		return false;
	}

	public BlockMaterial setOpaque(boolean opaque) {
	
		return this;
	}

	public int getLightLevel() {	
		return 15;
	}

	public BlockMaterial setLightLevel(int level) {		
		return this;
	}

}
