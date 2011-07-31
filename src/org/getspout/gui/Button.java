package org.getspout.gui;

public interface Button extends Control, Label{
	
	/**
	 * Get's the text that is displayed when the control is disabled
	 * @return disabled text
	 */
	public String getDisabledText();
	
	/**
	 * Sets the text that is displayed when the control is disabled
	 * @param text to display
	 * @return Button
	 */
	public Button setDisabledText(String text);
	
	/**
	 * Get's the hex color of the control while the mouse is hovering over it
	 * @return hex color
	 */
	public int getHoverColor();
	
	/**
	 * Sets the hex color of the control while the mouse is hovering over it
	 * @param hexColor
	 * @return
	 */
	public Button setHoverColor(int hexColor);

}
