/*
 * This file is part of SpoutAPI (http://www.spout.org/).
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

/**
 * The GenericPopup class creates an mouseable area where you can put multiple
 * Widgets.
 *
 * Optionally the background of the popup can be darkened to make it more
 * obvious that it is a popup.
 */
public interface Popup {

	/**
	 * Is true if the popup screen has no transparency layer
	 * @return transparency
	 */
	public boolean isTransparent();

	/**
	 * Sets the transparency layer
	 * @param value to set
	 * @return popupscreen
	 */
	public Popup setTransparent(boolean value);

	/**
	 * Closes the screen. Functionally equivelent to InGameHUD.closePopup()
	 * @return true if the screen was closed
	 */
	public boolean close();
}
