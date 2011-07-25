package org.bukkitcontrib.gui;

public interface PopupScreen extends Screen{
	
	public boolean isTransparent();
	
	public PopupScreen setTransparent(boolean value);
	
	public void close();
}
