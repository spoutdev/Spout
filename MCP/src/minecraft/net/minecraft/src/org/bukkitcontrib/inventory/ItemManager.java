package org.bukkitcontrib.inventory;

public interface ItemManager {
	
	/**
	 * Gets notchian item name for the item, or the custom name if one overrides it
	 * @param item to get the name of
	 * @return name
	 */
	public String getItemName(int item);
	
	/**
	 * Gets  the custom name of the item, or null if none exists
	 * @param item to get the name of
	 * @return name
	 */
	public String getCustomItemName(int item);
	
	/**
	 * Gets notchian item name for the item, or the custom name if one overrides it
	 * @param item to get the name of
	 * @param data for the item
	 * @return name
	 */
	public String getItemName(int item, short data);
	
	/**
	 * Gets  the custom name of the item, or null if none exists
	 * @param item to get the name of
	 * @param data for the item
	 * @return name
	 */
	public String getCustomItemName(int item, short data);
	
	/**
	 * Sets the name of the item
	 * @param item to name
	 * @param name to set
	 */
	public void setItemName(int item, String name);
	
	/**
	 * Sets the name of the item
	 * @param item to name
	 * @param data of the item
	 * @param name to set
	 */
	public void setItemName(int item, short data, String name);
	
	/**
	 * Resets the name of the item back to the notchian default
	 * @param item to reset
	 */
	public void resetName(int item);
	
	/**
	 * Resets the name of the item back to the notchian default
	 * @param item to reset
	 * @param data of the item
	 */
	public void resetName(int item, short data);
	
	/**
	 * Resets the names of all items to the notchian defaults. Use with care.
	 */
	public void reset();
}
