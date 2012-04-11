package org.spout.api.gui;

public interface Control extends Widget, MouseEventHandler, KeyboardEventHandler {
	
	/**
	 * Sets the focus with FocusReason.GENERIC_REASON
	 * @return if the control wants to be focussed, if not, revert possible changes
	 */
	public boolean setFocus();
	
	/**
	 * Sets the focus while providing a reason.
	 * @param reason the reason why it should focus
	 * @return if the control wants to be focussed, if not, revert possible changes
	 */
	public boolean setFocus(FocusReason reason);
	
	/**
	 * Gets if the control has keyboard focus.
	 * @return if the control has keyboard focus
	 */
	public boolean hasFocus();
	
	/**
	 * Sets if the control is enabled. 
	 * If a widget is not enabled, you can't interact with it and it should render with a different style suggesting that to the user.
	 * @param enable
	 * @return the instance for chained calls
	 */
	public Control setEnabled(boolean enable);
	
	/**
	 * Gets if a control is enabled.
	 * @return the enabled property
	 */
	public boolean isEnabled();
}
