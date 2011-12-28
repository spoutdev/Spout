package org.getspout.api.inventory;

import java.io.Serializable;

public class Inventory implements Serializable {

	private static final long serialVersionUID = 0L;
	private ItemStack[] contents;

	public Inventory(int size) {
		contents = new ItemStack[size];
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public ItemStack getItem(int slot) {
		return contents[slot];
	}

	public void setItem(ItemStack item, int slot) {
		contents[slot] = item;
	}

	public boolean addItem(ItemStack item) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) {
				contents[i] = item;
				return true;
			}
		}
		return false;
	}
}
