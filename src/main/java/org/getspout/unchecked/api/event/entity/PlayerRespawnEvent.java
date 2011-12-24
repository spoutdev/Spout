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
package org.getspout.unchecked.api.event.entity;

import org.getspout.api.entity.Entity;
import org.getspout.api.event.HandlerList;

/**
 * Called when a player respawns.
 */
public class PlayerRespawnEvent extends EntitySpawnEvent {
	private static HandlerList handlers = new HandlerList();

	private boolean bedRespawn;

	public Entity getPlayer() {
		return getEntity();
	}

	/**
	 * Returns true if the respawn location is a bed.
	 *
	 * @return True if the respawn location is a bed.
	 */
	public boolean isBedRespawn() {
		return bedRespawn;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}