/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.event.storage;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.player.Player;

/**
 * Called when data about a player needs to be loaded, usually right after a player session begins.
 */
public class PlayerLoadEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private boolean loaded = false;
	private Player player;
	public PlayerLoadEvent(Player player) {
		this.player = player;
	}
	
	/**
	 * Gets the player whose data is being loaded.
	 * 
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * True if a plugin has already loaded this data.
	 * 
	 * @return loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Sets the loaded state of this event. 
	 * 
	 * If the data is not reported loaded after it has been called, it will be loaded by the default save handler.
	 * 
	 * @param save
	 */
	public void setLoaded(boolean save) {
		loaded = save;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
