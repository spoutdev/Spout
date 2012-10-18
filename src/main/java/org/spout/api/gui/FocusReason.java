package org.spout.api.gui;

public enum FocusReason {
	/**
	 * Invoked focus when the user presses the TAB-key on the keyboard and it switches to the next control
	 */
	KEYBOARD_TAB,
	/**
	 * Invoked focus when the user clicks on a control
	 */
	CLICKED,
	/**
	 * Invoked when the focus was set by a plugin
	 */
	PROGRAMMED,
	;
}
