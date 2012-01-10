/*
 * This file is part of Spout API (http://wiki.getspout.org/).
 * 
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import java.util.Set;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.spout.api.player.SpoutPlayer;

/**
 * This defines the basic Screen, but should not be used directly.
 */
public interface Screen extends Widget {

	/**
	 * Get's an array of all the attached widgets to this screen. Modifying this array will not affect the screen.
	 * @return array of all widgets
	 */
	public Widget[] getAttachedWidgets();

	/**
	 * Attaches a widget to this screen
	 * @param plugin that owns this widget
	 * @param widget to attach
	 * @return screen
	 */
	public Screen attachWidget(Plugin plugin, Widget widget);

	/**
	 * Attach a series of widgets to this screen in one call.
	 * @param plugin that owns these widgets
	 * @param widgets to attach
	 * @return screen
	 */
	public Screen attachWidgets(Plugin plugin, Widget... widgets);

	/**
	 * Removes a widget from this screen
	 * @param widget to remove
	 * @return screen
	 */
	public Screen removeWidget(Widget widget);

	/**
	 * Removes all of a plugin's widgets from this screen
	 * @param widget to remove
	 * @return screen
	 */
	public Screen removeWidgets(Plugin plugin);

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

	/**
	 * Is true if this grey background is visible and rendering on the screen
	 * @return visible
	 */
	public boolean isBgVisible();

	/**
	 * Get the player the screen is attached to
	 * @return spout player
	 */
	public SpoutPlayer getPlayer();

	/**
	 * Sets the visibility of the grey background. If true, it will render normally. If false, it will not appear on the screen.
	 * @param enable the visibility
	 * @return the screen
	 */
	public Screen setBgVisible(boolean enable);

	/**
	 * Gets the screen type of this screen
	 * @return the screen type
	 */
	public ScreenType getScreenType();

	public Set<Widget> getAttachedWidgetsAsSet(boolean recursive);
}
