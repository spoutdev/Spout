package org.getspout.api.material.item;

import org.getspout.api.material.Food;

public class GenericFood extends GenericItemMaterial implements Food {
	private final int hunger;

	public GenericFood(String name, int id, int hunger) {
		super(name, id);
		this.hunger = hunger;
	}

	public int getHungerRestored() {
		return hunger;
	}

}
