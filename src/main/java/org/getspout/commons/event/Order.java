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
package org.getspout.commons.event;

/**
 * Order of event listener calls.
 * 
 * Odd-numbered slots are called even when events are marked "not propogating". If an event stops propogating partway through an even slot, that slot will not cease execution, but future even slots will not be called.
 * 
 * @author lahwran
 */
public enum Order {

	/**
	 * Called before all other handlers. Should be used for high-priority event canceling.
	 */
	Earliest(0),

	/**
	 * Called after "Earliest" handlers and before "Early" handlers. Is called even when event has been canceled. Should generally be used to uncancel events canceled in Earliest.
	 */
	EarlyIgnoreCancelled(1),

	/**
	 * Called after "Earliest" handlers. Should generally be used for low priority event canceling.
	 */
	Early(2),

	/**
	 * Called after "Early" handlers and before "Default" handlers. Is called even when event has been canceled. This is for general-purpose always-run events.
	 */
	DefaultIgnoreCancelled(3),
	/**
	 * Default call, for general purpose handlers
	 */
	Default(4),

	/**
	 * Called after "Default" handlers and before "Late" handlers. Is called even when event has been canceled.
	 */
	LateIgnoreCancelled(5),

	/**
	 * Called after "Default" handlers.
	 */
	Late(6),

	/**
	 * Called after "Late" handlers and before "Latest" handlers. Is called even when event has been canceled.
	 */
	LatestIgnoreCancelled(7),

	/**
	 * Called after "Late" handlers.
	 */
	Latest(8),

	/**
	 * Called after "Latest" handlers. No changes to the event should be made in this order slot (though it is not enforced). Is called even when event has been cancelled.
	 */
	Monitor(9);

	private int index;

	Order(int index) {
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
}
