package org.getspout.commons.material.item;

import org.getspout.commons.material.Item;

public class GenericItem implements Item {

	private final int id;
	private final int data;
	private final boolean subtypes;
	private final String name;
	private String customName;

	public GenericItem(String name, int id, int data, boolean subtypes) {
		this.name = name;
		this.id = id;
		this.data = data;
		this.subtypes = subtypes;
	}

	protected GenericItem(String name, int id, int data) {
		this(name, id, data, false);
	}

	public GenericItem(String name, int id) {
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
