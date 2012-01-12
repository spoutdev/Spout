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


public interface MinecraftFont {

	/**
	 * Gets the scaled width of the text, in terms of the minecraft screen resolution
	 * 
	 * @param text
	 * @return width
	 */
	public int getTextWidth(String text);

	/**
	 * Is true if the character can be be sent via chat or rendered on the screen
	 * 
	 * @param ch to check
	 * @return if the character can be be sent via chat or rendered on the screen
	 */
	public boolean isAllowedChar(char ch);

	/**
	 * Is true if all of the text can be sent via chat or rendered on the screen
	 * 
	 * @param text to check
	 * @return if all of the text can be sent via chat or rendered on the screen
	 */
	public boolean isAllowedText(String text);

	/**
	 * Draws the given text onto the screen at the given x and y coordinates, with the given hexidecimal color
	 * 
	 * @param text to draw
	 * @param x to position the left lower corner at
	 * @param y to position the left lower corner at
	 * @param color, in 0XFFFFFF format (2 bytes for red, 2 bytes for green, 2 bytes for blue)
	 */
	public void drawString(String text, int x, int y, int color);

	/**
	 * Draws the given text centered onto the screen at the given x and y coordinates, with the given hexidecimal color
	 * 
	 * @param text to draw
	 * @param x to position the left lower corner at
	 * @param y to position the left lower corner at
	 * @param color, in 0XFFFFFF format (2 bytes for red, 2 bytes for green, 2 bytes for blue)
	 */
	public void drawCenteredString(String text, int x, int y, int color);
}
