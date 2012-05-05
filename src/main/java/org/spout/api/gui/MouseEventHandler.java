package org.spout.api.gui;

import java.awt.Point;

public interface MouseEventHandler {
	/**
	 * Called when the user presses a mouse button at a particular position
	 * @param position where the cursor is
	 * @param button the button that got pressed
	 */
	public void onMouseDown(Point position, MouseButton button);
	
	/**
	 * Called when the user moves the cursor
	 * @param from the old position of the cursor
	 * @param to the new position of the cursor
	 */
	public void onMouseMove(Point from, Point to);
	
	/**
	 * Called when the user releases a mouse button at a particular position, this is also called when the mouse has left the boundaries of the MouseEventHandler
	 * @param position where the cursor is
	 * @param button the button that was released
	 */
	public void onMouseUp(Point position, MouseButton button);
}
