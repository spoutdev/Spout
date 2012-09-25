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
 * Order of an event listener may be registered at.
 * <p/>
 * Odd-numbered (IGNORE_CANCELLED) slots are called even when events are marked "not propagating".<br/>
 * If an event stops propagating part way through an even slot, that slot will not cease execution,
 * but future even slots will not be called.
 */
public enum Order {
	/**
	 * Called before all other handlers. Should be used for high-priority event canceling.
	 */
	EARLIEST(0, false),
	/**
	 * Called after "Earliest" handlers and before "Early" handlers.<br/>
	 * Is called even when event has been canceled.<br/>
	 * Should generally be used to uncancel events canceled in Earliest.<br/>
	 */
	EARLY_IGNORE_CANCELLED(1, true),
	/**
	 * Called after "Earliest" handlers. Should generally be used for low priority event canceling.
	 */
	EARLY(2, false),
	/**
	 * Called after "Early" handlers and before "Default" handlers.<br/>
	 * Is called even when event has been canceled. <br/>
	 * This is for general-purpose always-run events.<br/>
	 */
	DEFAULT_IGNORE_CANCELLED(3, true),
	/**
	 * Default call, for general purpose handlers
	 */
	DEFAULT(4, false),
	/**
	 * Called after "Default" handlers and before "Late" handlers.<br/>
	 * Is called even when event has been canceled.<br/>
	 */
	LATE_IGNORE_CANCELLED(5, true),
	/**
	 * Called after "Default" handlers.
	 */
	LATE(6, false),
	/**
	 * Called after "Late" handlers and before "Latest" handlers. <br/>
	 * Is called even when event has been canceled.<br/>
	 */
	LATEST_IGNORE_CANCELLED(7, true),
	/**
	 * Called after "Late" handlers.
	 */
	LATEST(8, false),
	/**
	 * Called after "Latest" handlers. <br/>
	 * No changes to the event should be made in this order slot (though it is not enforced).<br/>
	 * This is called even when event has been cancelled.</br>
	 */
	MONITOR(9, true);
	private final int index;
	private final boolean ignoreCancelled;

	Order(int index, boolean ignoreCancelled) {
		this.index = index;
		this.ignoreCancelled = ignoreCancelled;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return whether this Order ignores cancellation status
	 */
	public boolean ignoresCancelled() {
		return ignoreCancelled;
	}
}
