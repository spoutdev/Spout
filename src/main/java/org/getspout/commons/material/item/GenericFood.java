package org.getspout.commons.material.item;

import org.getspout.commons.material.Food;
import org.getspout.commons.material.item.GenericItem;

public class GenericFood extends GenericItem implements Food {
	private final int hunger;
	public GenericFood(String name, int id, int hunger) {
		super(name, id);
		this.hunger = hunger;
	}

	public int getHungerRestored() {
		return hunger;
	}

}
