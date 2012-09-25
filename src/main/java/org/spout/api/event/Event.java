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
package org.spout.api.event;

/**
 * Represents a callable event.
 */
public abstract class Event {
	/**
	 * Stores cancelled status. will be false unless a subclass publishes
	 * setCancelled.
	 */
	protected boolean cancelled = false;
	/**
	 * Stores whether this event has already been called
	 */
	private boolean beenCalled = false;

	/**
	 * Get the static handler list of this event subclass.
	 * @return HandlerList to call event with
	 */
	public abstract HandlerList getHandlers();

	/**
	 * Get event type name.
	 * @return event name
	 */
	protected String getEventName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getEventName() + " (" + this.getClass().getName() + ")";
	}

	/**
	 * Set cancelled status. Events which wish to be cancellable should
	 * implement Cancellable and implement setCancelled as:
	 * <p/>
	 * <pre>
	 * public void setCancelled(boolean cancelled) {
	 * 	super.setCancelled(cancelled);
	 * }
	 * </pre>
	 * @param cancelled True to cancel event
	 */
	protected void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Returning true will prevent calling any even {@link Order}ed slots.
	 * @return false if the event is propogating; events which do not implement Cancellable should never return true here.
	 * @see Order
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @return true if the event has already been called by the {@link EventManager}, otherwise false.
	 */
	public boolean hasBeenCalled() {
		return beenCalled;
	}

	void setHasBeenCalled(boolean beenCalled) {
		this.beenCalled = beenCalled;
	}
}
