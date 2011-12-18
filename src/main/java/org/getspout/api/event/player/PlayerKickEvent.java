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

import org.getspout.api.event.Cancellable;

/**
 * Called when a player gets kicked from the server
 */
public class PlayerKickEvent extends PlayerLeaveEvent implements Cancellable {
	private String kickReason;

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public boolean isKick() {
		return true;
	}

	@Override
	public boolean isQuit() {
		return false;
	}

	/**
	 * Gets the reason why the player is getting kicked
	 *
	 * @return string kick reason
	 */
	public String getKickReason() {
		return kickReason;
	}

	/**
	 * Sets the reason why the player is getting kicked
	 *
	 * @param kickReason kick reason
	 */
	public void setKickReason(String kickReason) {
		this.kickReason = kickReason;
	}

}