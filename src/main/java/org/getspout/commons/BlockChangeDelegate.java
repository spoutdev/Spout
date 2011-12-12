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
/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
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
package org.getspout.commons;

/**
 * A delegate for handling block changes. This serves as a direct interface
 * between generation algorithms in the client implementation and utilizing
 * code.
 *
 * @author sk89q
 */
public interface BlockChangeDelegate {

	/**
	 * Set a block type at the specified coordinates.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param typeId New block ID
	 * @return true if the block was set successfully
	 */
	public boolean setRawTypeId(int x, int y, int z, int typeId);

	/**
	 * Set a block type and data at the specified coordinates.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param typeId New block ID
	 * @param data Block data
	 * @return true if the block was set successfully
	 */
	public boolean setRawTypeIdAndData(int x, int y, int z, int typeId, int data);

	/**
	 * Get the block type at the location.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The block ID
	 */
	public int getTypeId(int x, int y, int z);

	/**
	 * Gets the height of the world.
	 *
	 * @return Height of the world
	 */
	public int getHeight();
}
