package org.bukkitcontrib.gui;

public interface ItemWidget extends Widget{
	
	public ItemWidget setTypeId(int id);
	
	public int getTypeId();
	
	public ItemWidget setData(short data);
	
	public short getData();
	
	public ItemWidget setDepth(int depth);
	
	public int getDepth();
	
	public ItemWidget setWidth(int width);
	
	public ItemWidget setHeight(int height);
	
}
