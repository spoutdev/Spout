package org.getspout.Spout.gui;

public interface TextField extends Control{
	
	public int getCursorPosition();
	
	public TextField setCursorPosition(int position);
	
	public String getText();
	
	public TextField setText(String text);
	
	public int getMaximumCharacters();
	
	public TextField setMaximumCharacters(int max);
	
	public int getFieldColor();
	
	public TextField setFieldColor(int hex);
	
	public int getBorderColor();
	
	public TextField setBorderColor(int hex);

}
