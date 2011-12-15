/*
 * This file is part of SpoutCommons (http://wiki.getspout.org/).
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
package org.getspout.api;

/**
 * Represents a player, offline or not.
 */
public interface OfflinePlayer {

	/**
	 * Returns true if this player is online
	 * @return online
	 */
	public boolean isOnline();

	/**
	 * Returns the name of this player
	 *
	 * @return Player name
	 */
	public String getName();

}
