package org.bukkitcontrib.inventory;

public interface ItemManager {
	
	public String getItemName(int item);
	
	public String getCustomItemName(int item);
	
	public String getItemName(int item, byte data);
	
	public String getCustomItemName(int item, byte data);
	
	public void setItemName(int item, String name);
	
	public void setItemName(int item, byte data, String name);
	
	public void resetName(int item);
	
	public void resetName(int item, byte data);
	
	public void reset();
}
