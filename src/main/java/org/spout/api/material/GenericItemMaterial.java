package org.spout.api.material;

import org.spout.api.model.Model;

public class GenericItemMaterial implements ItemMaterial {

	private final short id;
	private final short data;
	private final boolean subtypes;
	private final String name;
	private Model model;
	private String displayName;

	public GenericItemMaterial(String name, int id, int data, boolean subtypes) {
		this.name = name;
		this.displayName = name;
		this.id = (short) id;
		this.data = (short) data;
		this.subtypes = subtypes;

		MaterialData.registerMaterial(this);
	}

	protected GenericItemMaterial(String name, int id, int data) {
		this(name, id, data, false);
	}

	public GenericItemMaterial(String name, int id) {
		this(name, id, 0, false);
	}

	public short getId() {
		return id;
	}

	public short getData() {
		return data;
	}

	public boolean hasSubtypes() {
		return subtypes;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Material setModel(Model model) {
		this.model = model;
		return this;
	}

	public Model getModel() {
		return model;
	}

	public void onInventoryRender() {
		// TODO Auto-generated method stub
	}
}
