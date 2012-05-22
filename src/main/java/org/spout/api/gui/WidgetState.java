package org.spout.api.gui;

public enum WidgetState {
	/**
	 * The widget has focus. Should draw a border in an attractive color around it
	 */
	FOCUSSED(0),
	/**
	 * The widget is not in focus.
	 */
	UNFOCUSSED(1),
	/**
	 * The widgets screen is not in focus.
	 */
	INACTIVE(2),
	;
	
	private int id;

	private WidgetState(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
