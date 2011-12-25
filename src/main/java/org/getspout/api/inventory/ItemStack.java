package org.getspout.api.inventory;

import java.util.Map;

import org.getspout.api.io.nbt.Tag;
import org.getspout.api.material.ItemMaterial;

/**
 * Represents a stack of items
 */
public class ItemStack {
	
	private ItemMaterial material;
	private int amount;
	private short damage;
	private Map<String, Tag> auxData;
	
	/**
	 * Gets the Material of the stack
	 * 
	 * @return the material
	 */
	public ItemMaterial getMaterial() {
		return material;
	}
	
	/**
	 * Sets the Material for the stack
	 * 
	 * @param material the material
	 */
	public void setMaterial(ItemMaterial material) {
		this.material = material;
	}
	
	/**
	 * Gets the amount of the Material contained in the item stack
	 * 
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Sets amount of the Material contained in the item stack
	 * 
	 * @param amount the amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 * Gets the damage value for the item
	 * 
	 * @return the damage
	 */
	public short getDamage() {
		return damage;
	}
	
	/**
	 * Sets the damage for the item stack
	 * 
	 * @param damage the damage
	 */
	public void setDamage(short damage) {
		this.damage = damage;
	}

	/**
	 * returns a copy of the map containing the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public Map<String, Tag> getAuxData() {
		return Tag.cloneMap(auxData);
	}
	
	/**
	 * Sets the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public void setAuxData(Map<String, Tag> auxData) {
		this.auxData = Tag.cloneMap(auxData);
	}

}
