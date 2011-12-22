package org.getspout.api.material.block;

import org.getspout.api.material.BlockMaterial;
import org.getspout.unchecked.api.render.BlockDesign;

public class GenericBlockMaterial implements BlockMaterial {
	private final int id;
	private final int data;
	private final boolean subtypes;
	private final String name;
	private String customName;
	private BlockDesign design;

	private GenericBlockMaterial(String name, int id, int data, boolean subtypes) {
		this.name = name;
		this.id = id;
		this.data = data;
		this.subtypes = subtypes;
	}

	protected GenericBlockMaterial(String name, int id, int data) {
		this(name, id, data, true);
	}

	protected GenericBlockMaterial(String name, int id) {
		this(name, id, 0, false);
	}

	public int getRawId() {
		return id;
	}

	public int getRawData() {
		return data;
	}

	public boolean hasSubtypes() {
		return subtypes;
	}

	public String getName() {
		if (customName != null) {
			return customName;
		}
		return name;
	}

	public String getNotchianName() {
		return name;
	}

	public void setName(String name) {
		customName = name;
	}

	public float getFriction() {
		//		return Spout.getGame().getMaterialManager().getFriction(this);
		return 0;
	}

	public BlockMaterial setFriction(float friction) {
		//		Spoutcraft.getClient().getMaterialManager().setFriction(this, friction);
		return this;
	}

	public float getHardness() {
		//		return Spoutcraft.getClient().getMaterialManager().getHardness(this);
		return 0;
	}

	public BlockMaterial setHardness(float hardness) {
		//		Spoutcraft.getClient().getMaterialManager().setHardness(this, hardness);
		return this;
	}

	public boolean isOpaque() {
		//		return Spoutcraft.getClient().getMaterialManager().isOpaque(this);
		return false;
	}

	public BlockMaterial setOpaque(boolean opaque) {
		//		Spoutcraft.getClient().getMaterialManager().setOpaque(this, opaque);
		return this;
	}

	public int getLightLevel() {
		//		return Spoutcraft.getClient().getMaterialManager().getLightLevel(this);
		return 0;
	}

	public BlockMaterial setLightLevel(int level) {
		//		Spoutcraft.getClient().getMaterialManager().setLightLevel(this, level);
		return this;
	}

	public BlockDesign getBlockDesign() {
		return design;
	}

	public BlockMaterial setBlockDesign(BlockDesign design) {
		this.design = design;
		return this;
	}
}
