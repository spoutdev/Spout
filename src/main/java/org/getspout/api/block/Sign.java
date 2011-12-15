/*
 * This file is part of Bukkit (http://bukkit.org/).
 * 
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.block;

public interface Sign extends BlockState {

	/**
	 * Gets all the lines of text currently on this sign.
	 *
	 * @return Array of Strings containing each line of text
	 */
	public String[] getLines();

	/**
	 * Gets the line of text at the specified index.
	 *
	 * For example, getLine(0) will return the first line of text.
	 *
	 * @param index Line number to get the text from, starting at 0
	 * @throws IndexOutOfBoundsException Thrown when the line does not exist
	 * @return Text on the given line
	 */
	public String getLine(int index) throws IndexOutOfBoundsException;

	/**
	 * Sets the line of text at the specified index.
	 *
	 * For example, setLine(0, "Line One") will set the first line of text to
	 * "Line One".
	 *
	 * @param index Line number to set the text at, starting from 0
	 * @param line New text to set at the specified index
	 * @throws IndexOutOfBoundsException
	 */
	public void setLine(int index, String line) throws IndexOutOfBoundsException;
}
