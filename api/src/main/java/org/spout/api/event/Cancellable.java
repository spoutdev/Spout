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
package org.spout.api.event;

/**
 * Interface for events that can be cancelled, to prevent them from propagating to downstream handlers.
 */
public interface Cancellable {
	/**
	 * If an event stops propagating (ie, is cancelled) partway through an even slot, that slot will not cease execution, but future even slots will not be called.
	 *
	 * @param cancelled True to set event as cancelled, false to set as not cancelled.
	 */
	public void setCancelled(boolean cancelled);

	/**
	 * Get event cancelled state.
	 *
	 * @return whether event is cancelled
	 */
	public boolean isCancelled();
}
