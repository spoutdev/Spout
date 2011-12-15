/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
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
package org.getspout.api.event;

/**
 * Order of event listener calls.
 * <p/>
 * Odd-numbered slots are called even when events are marked "not propogating". If an event stops propogating partway through an even slot, that slot will not cease execution, but future even slots will not be called.
 *
 * @author lahwran
 */
public enum Order {

	/**
	 * Called before all other handlers. Should be used for high-priority event canceling.
	 */
	EARLIEST(0, false),

	/**
	 * Called after "Earliest" handlers and before "Early" handlers. Is called even when event has been canceled. Should generally be used to uncancel events canceled in Earliest.
	 */
	EARLY_IGNORE_CANCELLED(1, true),

	/**
	 * Called after "Earliest" handlers. Should generally be used for low priority event canceling.
	 */
	EARLY(2, false),

	/**
	 * Called after "Early" handlers and before "Default" handlers. Is called even when event has been canceled. This is for general-purpose always-run events.
	 */
	DEFAULT_IGNORE_CANCELLED(3, true),
	/**
	 * Default call, for general purpose handlers
	 */
	DEFAULT(4, false),

	/**
	 * Called after "Default" handlers and before "Late" handlers. Is called even when event has been canceled.
	 */
	LATE_IGNORE_CANCELLED(5, true),

	/**
	 * Called after "Default" handlers.
	 */
	LATE(6, false),

	/**
	 * Called after "Late" handlers and before "Latest" handlers. Is called even when event has been canceled.
	 */
	LATEST_IGNORE_CANCELLED(7, true),

	/**
	 * Called after "Late" handlers.
	 */
	LATEST(8, false),

	/**
	 * Called after "Latest" handlers. No changes to the event should be made in this order slot (though it is not enforced). Is called even when event has been cancelled.
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
	 *
	 * @return whether this Order ignores cancellation status
	 */
	public boolean ignoresCancelled() {
		return ignoreCancelled;
	}
}
