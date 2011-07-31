package org.getspout.gui;

public interface Button extends Control, Label{
	
	public String getDisabledText();
	
	public Button setDisabledText(String text);
	
	public int getHoverColor();
	
	public Button setHoverColor(int hexColor);

}
