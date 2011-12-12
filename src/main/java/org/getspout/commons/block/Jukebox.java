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
package org.getspout.commons.block;

import org.getspout.commons.material.Item;

/**
 * Represents a Jukebox
 */
public interface Jukebox extends BlockState {
	/**
	 * Get the record currently playing
	 * @return The record Material, or AIR if none is playing
	 */
	public Item getPlaying();
	
	/**
	 * Set the record currently playing
	 * @param record The record Material, or null/AIR to stop playing
	 */
	public void setPlaying(Item record);
	
	/**
	 * Check if the jukebox is currently playing a record
	 * @return True if there is a record playing
	 */
	public boolean isPlaying();
	
	/**
	 * Stop the jukebox playing and eject the current record
	 * @return True if a record was ejected; false if there was none playing
	 */
	public boolean eject();
}
