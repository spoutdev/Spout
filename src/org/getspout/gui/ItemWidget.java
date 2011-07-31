package org.getspout.gui;

public interface ItemWidget extends Widget{
	/**
	 * Sets the type id of this item widget
	 * @param id
	 * @return ItemWidget
	 */
	public ItemWidget setTypeId(int id);
	
	/**
	 * Gets the type id of this item widget
	 * @return type id
	 */
	public int getTypeId();
	
	/**
	 * Sets the data of this item widget
	 * @param data to set
	 * @return ItemWidget
	 */
	public ItemWidget setData(short data);
	
	/**
	 * Gets the data of this item widget, is zero by default
	 * @return data
	 */
	public short getData();
	
	/**
	 * Sets the z render depth for this 3-d item widget
	 * @param depth to render at
	 * @return ItemWidget
	 */
	public ItemWidget setDepth(int depth);
	
	/**
	 * Gets the z render depth for this 3-d item widget
	 * @return depth
	 */
	public int getDepth();
	
	public ItemWidget setWidth(int width);
	
	public ItemWidget setHeight(int height);
}
