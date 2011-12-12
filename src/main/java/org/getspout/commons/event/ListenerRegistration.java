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

import org.getspout.commons.addon.Addon;
import org.getspout.commons.event.Event;
import org.getspout.commons.event.Listener;
import org.getspout.commons.event.Order;

/**
 * @author lahwran
 * @param <TEvent> Event class
 */
public class ListenerRegistration<TEvent extends Event<TEvent>> {
	private final Listener<TEvent> listener;
	private final Order orderslot;
	private final Addon addon;

	/**
	 * 
	 * @param listener Listener this registration represents
	 * @param orderslot Order position this registration is in
	 * @param addon addon that created this registration
	 */
	public ListenerRegistration(final Listener<TEvent> listener, final Order orderslot, final Addon addon) {
		this.listener = listener;
		this.orderslot = orderslot;
		this.addon = addon;
	}

	/**
	 * Gets the listener for this registration
	 * 
	 * @return Registered Listener
	 */
	public Listener<TEvent> getListener() {
		return listener;
	}

	/**
	 * Gets the Addon for this registration
	 * 
	 * @return Registered Addon
	 */
	public Addon getAddon() {
		return addon;
	}

	/**
	 * Gets the order slot for this registration
	 * 
	 * @return Registered order
	 */
	public Order getOrder() {
		return orderslot;
	}
}
