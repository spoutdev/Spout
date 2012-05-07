package org.spout.api.gui;

public interface Screen extends MouseEventHandler, KeyboardEventHandler, Container, Widget {
	/**
	 * Sets the focussed control
	 * @param control the control in focus 
	 * @return the instance to allow chained calls
	 */
	public Screen setFocussedControl(Control control);
	
	/**
	 * Gets the control in focus
	 * @return the control in focus
	 */
	public Control getFocussedControl();
	
	public ScreenType getScreenType();
}
