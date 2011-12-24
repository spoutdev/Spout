package org.getspout.api.inventory;

import java.util.Map;

import org.getspout.api.io.nbt.Tag;
import org.getspout.api.material.ItemMaterial;

/**
 * Represents a stack of items
 */
public interface ItemStack {
	
	/**
	 * Gets the Material of the stack
	 * 
	 * @return the material
	 */
	public ItemMaterial getMaterial();
	
	/**
	 * Sets the Material for the stack
	 * 
	 * @param material the material
	 */
	public void setMaterial(ItemMaterial material);
	
	/**
	 * Gets the amount of the Material contained in the item stack
	 * 
	 * @return the amount
	 */
	public int getAmount();
	
	/**
	 * Sets amount of the Material contained in the item stack
	 * 
	 * @param amount the amount
	 */
	public void setAmount(int amount);
	
	/**
	 * Gets the damage value for the item
	 * 
	 * @return the amount
	 */
	public short getDamage();
	
	/**
	 * Sets the damage for the item stack
	 * 
	 * @param amount the amount
	 */
	public void setDamage(short damage);

	/**
	 * returns a copy of the map containing the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public Map<String, Tag> getAuxData();
	
	/**
	 * Sets the aux data for this stack
	 * 
	 * @return the aux data
	 */
	public void setAuxData(Map<String, Tag> auxData);
	

}
