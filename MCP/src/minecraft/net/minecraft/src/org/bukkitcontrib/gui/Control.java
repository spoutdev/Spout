package org.getspout.gui;

public interface Control extends Widget{

	public boolean isEnabled();
	
	public Control setEnabled(boolean enable);
	
	public String getHoverText();
	
	public Control setHoverText(String text);
	
	public int getColor();
	
	public Control setColor(int hexColor);
	
	public int getDisabledColor();
	
	public Control setDisabledColor(int hexColor);
}
