package org.getspout.gui;

public interface Label extends Widget{
	/**
	 * Gets the text of the label
	 * @return text
	 */
	public String getText();
	
	/**
	 * Sets the text of the label
	 * @param text to set
	 * @return label
	 */
	public Label setText(String text);
	
	/**
	 * True if the text for the label is centered
	 * @return centered
	 */
	public boolean isCentered();
	
	/**
	 * Sets the centered status of the text
	 * @param center the text
	 * @return label
	 */
	public Label setCentered(boolean center);
	
	/**
	 * Gets the hex color code for the text
	 * @return color code
	 */
	public int getHexColor();
	
	/** 
	 * Sets the hex color code for the text
	 * @param hex color code to set
	 * @return label
	 */
	public Label setHexColor(int hex);
}
