/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
/*
* This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.storage;

import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Called before an player data is saved to disk.
 * <p>
 * PlayerSaveEvents are async events and will never be executed on the
 * main thread. Be sure to synchronize your data structures.
 * </p>
 */
public class PlayerSaveEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private final PlayerSnapshot snapshot;
	private boolean handled = false;

	public PlayerSaveEvent(PlayerSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	/**
	 * Gets the player snapshot to be saved
	 * 
	 * @return snapshot
	 */
	public PlayerSnapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * True if the player snapshot has been successfully saved.
	 * <p>
	 * If isSaved() is false after the event has been fired, the engine will
	 * use the default NBT player saving process, if it is true, the engine will
	 * <b>NOT</b> save the player data and will rely on the plugin to restore the
	 * data at the next login.
	 * 
	 * @return handled
	 */
	public boolean isSaved() {
		return handled;
	}

	/**
	 * Sets whether this player snapshot has been saved.
	 * <p>
	 * If a plugin has saved the player snapshot and wants to override the
	 * default engine saving of player data, it should setSaved(true). 
	 * </p>
	 * @param handle
	 */
	public void setSaved(boolean handle) {
		this.handled = handle;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}