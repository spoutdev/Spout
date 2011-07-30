package org.bukkitcontrib.gui;

public interface PopupScreen extends Screen{
	
	public boolean isTransparent();
	
	public PopupScreen setTransparent(boolean value);
	
	public boolean isFocus();
	
	public PopupScreen setFocus(boolean focus);
	
	public boolean close();
}
