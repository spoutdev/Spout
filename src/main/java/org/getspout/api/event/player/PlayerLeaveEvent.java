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
package org.getspout.api.event.player;

import org.getspout.api.event.HandlerList;

/**
 * Called when a player leaves the server.
 */
public class PlayerLeaveEvent extends PlayerEvent {
	private static HandlerList handlers = new HandlerList();

	private String message;

	private boolean quit;

	/**
	 * Gets the message to be sent to all players when leaving.
	 * 
	 * @return 
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to be sent to all players when leaving.
	 * 
	 * @param message 
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets if this event was a quit.
	 * 
	 * @return True if the player quit.
	 */
	public boolean isQuit() {
		return quit;
	}

	/**
	 * Gets if the player was kicked.
	 * 
	 * @return True if the player was kicked.
	 */
	public boolean isKick() {
		return !quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
