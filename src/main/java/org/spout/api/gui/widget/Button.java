package org.spout.api.gui.widget;

import org.spout.api.gui.Control;

public interface Button extends Label, Control {
	/**
	 * Gets if the button is currently pushed in by a the left mouse button or the space key
	 * @return if the button is pushed in
	 */
	public boolean isDown();
	
	/**
	 * Gets if the button is currently checked
	 * @return if the button is checked
	 */
	public boolean isChecked();
	
	/**
	 * Sets if the button is currently checked
	 * @param check
	 * @return 
	 */
	public Button setChecked(boolean check);
	
	/**
	 * Sets if the button can be checked or not
	 * This property is disabled for PushButtons by default
	 * @param checkable
	 */
	public Button setCheckable(boolean checkable);
	
	/**
	 * Gets if the button can be checked
	 * @return if the button can be checked
	 */
	public boolean isCheckable();
	
	/**
	 * Clicks the button. If checkable, this will toggle the checked state
	 */
	public Button click();
	
	/**
	 * Clicks the button through holding it down for the given ticks and releasing it after that
	 * @param ticks the time to hold it in ticks
	 */
	public Button clickLong(int ticks);
}
