package org.getspout.api.inventory;

import java.util.Set;

public class CraftingInventory extends Inventory {
	
	private Set<String> allowedSubTypes;

	public CraftingInventory(int size) {
		super(size);
	}
	
	public Set<String> getAllowedSubTypes() {
		return allowedSubTypes;
	}
	
	public CraftingInventory addAllowedSubType(String subType) {
		allowedSubTypes.add(subType);
		return this;
	}
}
