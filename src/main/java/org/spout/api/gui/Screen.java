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
import org.spout.api.player.Player;
import org.spout.api.plugin.Plugin;

/**
 * This defines the basic Screen, but should not be used directly.
 */
public interface Screen extends Container {

	/**
	 * Is true if this grey background is visible and rendering on the screen
	 * @return visible
	 */
	public boolean isBgVisible();

	/**
	 * Get the player the screen is attached to
	 * @return spout player
	 */
	public Player getPlayer();

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
