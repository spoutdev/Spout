package org.getspout.commons.material.item;

import org.getspout.commons.material.Food;
import org.getspout.commons.material.item.GenericItemMaterial;

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
