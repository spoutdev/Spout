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

import java.util.Iterator;

import org.spout.api.UnsafeClass;

@UnsafeClass
public interface TextProcessor extends Iterable<String> {

	/**
	 * Gets the text handled by this processor
	 *
	 * @return text
	 */
	public String getText();

	/**
	 * Sets the text handled by this processor
	 *
	 * @param str new text
	 */
	public void setText(String str);

	/**
	 * Returns the text seperated in lines
	 *
	 * @return string iterator holding the lines
	 */
	public Iterator<String> iterator();

	/**
	 * Gets the position of the cursor. Position zero is the start of the text.
	 *
	 * @return position
	 */
	public int getCursor();

	/**
	 * Sets the position of the cursor
	 *
	 * @param position to set to
	 * @return textfield
	 */
	public void setCursor(int cursor);

	/**
	 * Gets a two dimensional cursor [y, x] where y is the line number and x the position of the cursor in this line
	 *
	 * @return 2D cursor
	 */
	public int[] getCursor2D();

	/**
	 * Gets the maximum characters
	 *
	 * @return maximum characters
	 */
	public int getMaximumCharacters();

	/**
	 * Sets the maximum characters
	 *
	 * @param max maximum characters
	 */
	public void setMaximumCharacters(int max);

	/**
	 * Gets the maximum lines
	 *
	 * @return maximum lines
	 */
	public int getMaximumLines();

	/**
	 * Sets the maximum lines
	 *
	 * @param max maximum lines
	 */
	public void setMaximumLines(int max);

	/**
	 * Gets the width
	 *
	 * @return width
	 */
	public int getWidth();

	/**
	 * Sets the width
	 *
	 * @param width
	 */
	public void setWidth(int width);

	/**
	 * Clears the contents
	 */
	public void clear();

	/**
	 * Handles the keyboard input
	 *
	 * @param key the key's char representation
	 * @param keyId the key's Id
	 * @return dirty (i.e. has the content changed / would a call of getText() return a different String than before?)
	 */
	public boolean handleInput(char key, int keyId);
}
