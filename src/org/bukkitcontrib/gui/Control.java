package org.bukkitcontrib.gui;

public interface Control extends Widget{

	public boolean isEnabled();
	
	public Control setEnabled(boolean enable);
	
	public String getHoverText();
	
	public Control setHoverText(String text);
}
