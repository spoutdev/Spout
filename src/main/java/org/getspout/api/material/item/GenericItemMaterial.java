package org.getspout.api.material.item;

import org.getspout.api.material.ItemMaterial;

public class GenericItemMaterial implements ItemMaterial {

	private final int id;
	private final int data;
	private final boolean subtypes;
	private final String name;
	private String customName;

	public GenericItemMaterial(String name, int id, int data, boolean subtypes) {
		this.name = name;
		this.id = id;
		this.data = data;
		this.subtypes = subtypes;
	}

	protected GenericItemMaterial(String name, int id, int data) {
		this(name, id, data, false);
	}

	public GenericItemMaterial(String name, int id) {
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
		if(customName != null) {
			return customName;
		}
		return name;
	}
	
	public String getNotchianName() {
		return name;
	}

	public void setName(String name) {
		this.customName = name;
	}
}
