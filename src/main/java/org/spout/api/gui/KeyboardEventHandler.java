package org.spout.api.gui;

import org.spout.api.keyboard.Keyboard;

public interface KeyboardEventHandler {
	/**
	 * Called when the user presses a key
	 * @param key the key that has been pressed
	 */
	public void onKeyPress(Keyboard key);
	
	/**
	 * Called when the user releases a key
	 * @param key the key that has been released
	 */
	public void onKeyRelease(Keyboard key);
}
