/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
 * Called before an player data is loaded from disk. <p> PlayerLoadEvents are async events and will never be executed on the main thread. Be sure to synchronize your data structures. </p>
 */
public class PlayerLoadEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private final String name;
	private PlayerSnapshot snapshot = null;

	public PlayerLoadEvent(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the player to load the data for
	 *
	 * @return player name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the player snapshot to be loaded. It may be null. <p> If the player snapshot is null after the event has been fired, the engine will use the default NBT player loading process. If the
	 * snapshot is not-null, it will use the snapshot to restore the player state. <br/><br/> Plugins that wish to override the default saving and loading process should set the snapshot. </p>
	 *
	 * @return snapshot
	 */
	public PlayerSnapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * Sets the player snapshot to be loaded. Setting the snapshot to null causes the engine to use the default NBT loading process. <p> If the player snapshot is null after the event has been fired, the
	 * engine will use the default NBT player loading process. If the snapshot is not-null, it will use the snapshot to restore the player state. <br/><br/> Plugins that wish to override the default
	 * saving and loading process should set the snapshot. </p>
	 *
	 * @return snapshot
	 */
	public void setSnapshot(PlayerSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}