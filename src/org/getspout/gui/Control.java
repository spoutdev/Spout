package org.getspout.gui;

public interface Control extends Widget{

	/**
	 * True if the control is enabled and can receive input
	 * @return enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Disables input to the control, but still allows it to be visible
	 * @param enable
	 * @return Control
	 */
	public Control setEnabled(boolean enable);
	
	/**
	 * Not implemented
	 * @return 
	 */
	public String getHoverText();
	
	/**
	 * Not Implemented
	 * @param text
	 * @return
	 */
	public Control setHoverText(String text);
	
	/**
	 * Gets the hex color of this control
	 * @return hex color
	 */
	public int getColor();
	
	/**
	 * Sets the hex color of this control
	 * @param hexColor to set
	 * @return Control
	 */
	public Control setColor(int hexColor);
	
	/**
	 * Gets the hex color of this control when it is disabled
	 * @return disabled color
	 */
	public int getDisabledColor();
	
	/**
	 * Sets the hex color of this control when it is disabled
	 * @param hexColor to set
	 * @return Control
	 */
	public Control setDisabledColor(int hexColor);
}
