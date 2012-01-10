/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
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

import org.spout.api.util.Color;

/**
 * This is the base class for all user input widgets.
 */
public interface Control extends Widget {

	/**
	 * True if the control is enabled and can receive input
	 * @return enabled
	 */
	public boolean isEnabled();

	/**
	 * Disables input to the control, but still allows it to be visible
	 * @param enable
	 * @return Control
	 */
	public Control setEnabled(boolean enable);

	/**
	 * Gets the color of this control
	 * @return color
	 */
	public Color getColor();

	/**
	 * Sets the color of this control
	 * @param color to set
	 * @return Control
	 */
	public Control setColor(Color color);

	/**
	 * Gets the color of this control when it is disabled
	 * @return disabled color
	 */
	public Color getDisabledColor();

	/**
	 * Sets the color of this control when it is disabled
	 * @param color to set
	 * @return Control
	 */
	public Control setDisabledColor(Color color);

	public boolean isFocus();

	public Control setFocus(boolean focus);
}
