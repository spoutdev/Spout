/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import org.spout.api.util.Color;

/**
 * The GenericButton class represents a Minecraft button with a label placed on it.
 */
public interface Button extends Control, Label {

	/**
	 * Get's the text that is displayed when the control is disabled
	 * @return disabled text
	 */
	public String getDisabledText();

	/**
	 * Sets the text that is displayed when the control is disabled
	 * @param text to display
	 * @return Button
	 */
	public Button setDisabledText(String text);

	/**
	 * Get's the color of the control while the mouse is hovering over it
	 * @return color
	 */
	public Color getHoverColor();

	/**
	 * Sets the color of the control while the mouse is hovering over it
	 * @param color
	 * @return Button
	 */
	public Button setHoverColor(Color color);

	@Override
	public Button setText(String text);

	@Override
	public Button setTextColor(Color color);

	@Override
	public Button setAuto(boolean auto);

	/**
	 * Fires when this button is clicked on the screen.
	 * 
	 * If this is not overridden in a subclass then this event will be sent
	 * to the screen listener.
	 * 
	 * @param event
	 */
//	public void onButtonClick(ButtonClickEvent event);
}
