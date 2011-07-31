package org.getspout.gui;

public interface PopupScreen extends Screen{
	
	/**
	 * Is true if the popup screen has no transparency layer
	 * @return transparency
	 */
	public boolean isTransparent();
	
	/**
	 * Sets the transparency layer
	 * @param value to set
	 * @return popupscreen
	 */
	public PopupScreen setTransparent(boolean value);
	
	/**
	 * Closes the screen. Functionally equivelent to InGameHUD.closePopup()
	 * @return true if the screen was closed
	 */
	public boolean close();
}
