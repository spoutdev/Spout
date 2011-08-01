package org.getspout.Spout.gui;

public interface Label extends Widget{
	
	public String getText();
	
	public Label setText(String text);
	
	public boolean isCentered();
	
	public Label setCentered(boolean center);
	
	public int getHexColor();
	
	public Label setHexColor(int hex);
}
