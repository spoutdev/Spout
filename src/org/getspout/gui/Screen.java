package org.getspout.gui;

import java.util.UUID;

public interface Screen extends Widget{
	
	/**
	 * Get's an array of all the attached widgets to this screen. Modifying this array will not affect the screen.
	 * @return array of all widgets
	 */
	public Widget[] getAttachedWidgets();
	
	/**
	 * Attaches a widget to this screen
	 * @param widget to attach
	 * @return screen
	 */
	public Screen attachWidget(Widget widget);
	
	/**
	 * Removes a widget from this screen
	 * @param widget to remove
	 * @return screen
	 */
	public Screen removeWidget(Widget widget);
	
	/**
	 * Is true if the screen has the given widget attached to it. Uses a linear search, takes O(n) time to complete.
	 * @param widget to search for
	 * @return true if the widget was found
	 */
	public boolean containsWidget(Widget widget);
	
	/**
	 * Is true if the screen has a widget with the given id attached to it. Uses a linear search, takes O(n) time to complete.
	 * @param id to search for
	 * @return true if the widget was found
	 */
	public boolean containsWidget(UUID id);
	
	/**
	 * Get's the widget that is associated with the given id, or null if none was found
	 * @param id to search for
	 * @return widget, or null if none found.
	 */
	public Widget getWidget(UUID id);
	
	/**
	 * Replaces any attached widget with the given widget's id with the new widget
	 * @param widget to replace with
	 * @return true if a widget was replaced
	 */
	public boolean updateWidget(Widget widget);
}
