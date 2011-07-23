package org.bukkitcontrib.gui;

public interface PopupScreen extends Screen{
	
	public boolean isTransparent();
	
	public PopupScreen setTransparent(boolean value);
	
	public String getBackgroundTextureUrl();
	
	public PopupScreen setBackgroundTextureUrl(String url);
	
	public int getBackgroundSolidColor();
	
	public PopupScreen setBackgroundSolidColor(int hexCode);

}
