/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
