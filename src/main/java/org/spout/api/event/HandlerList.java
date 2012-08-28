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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

/**
 * A list of event handlers, stored per-event.
 */
public class HandlerList {
	/**
	 * Handler array. This field being an array is the key to this system's
	 * speed.
	 */
	private ListenerRegistration[] handlers = null;

	/**
	 * Returns the Dynamic handler lists. <br/>
	 * These are changed using register() and unregister()<br/>
	 * Changes automatically baked to the handlers array any time they have changed..
	 * 
	 * @return map of Registered handlers
	 */
	private final EnumMap<Order, List<ListenerRegistration>> handlerSlots;

	/**
	 * List of all HandlerLists which have been created, for use in bakeAll()
	 * 
	 * @return the list of all Handlers.
	 */
	private static final ArrayList<HandlerList> ALL_LISTS = new ArrayList<HandlerList>();

	/**
	 * Bake all handler lists. Best used just after all normal event registration is complete.
	 */
	public static void bakeAll() {
		for (HandlerList h : ALL_LISTS) {
			h.bake();
		}
	}

	public static <T> void unregisterAll() {
		for (HandlerList h : ALL_LISTS) {
			for (List<ListenerRegistration> regs : h.handlerSlots.values()) {
				regs.clear();
			}
			h.handlers = null;
		}
	}

	public static void unregisterAll(Object plugin) {
		for (HandlerList h : ALL_LISTS) {
			h.unregister(plugin);
		}
	}

	/**
	 * Create a new handler list and initialize using EventPriority The
	 * HandlerList is then added to meta-list for use in bakeAll()
	 */
	public HandlerList() {
		handlerSlots = new EnumMap<Order, List<ListenerRegistration>>(Order.class);
		for (Order o : Order.values()) {
			handlerSlots.put(o, new ArrayList<ListenerRegistration>());
		}
		ALL_LISTS.add(this);
	}

	/**
	 * Register a new listener in this handler list
	 *
	 * @param listener listener to register
	 */
	public void register(ListenerRegistration listener) {
		if (handlerSlots.get(listener.getOrder()).contains(listener)) {
			throw new IllegalStateException("This listener is already registered to priority " + listener.getOrder().toString());
		}
		handlers = null;
		handlerSlots.get(listener.getOrder()).add(listener);
	}

	public void registerAll(Collection<ListenerRegistration> listeners) {
		for (ListenerRegistration listener : listeners) {
			register(listener);
		}
	}

	/**
	 * Remove a listener from a specific order slot
	 *
	 * @param listener listener to remove
	 */
	public void unregister(ListenerRegistration listener) {
		if (handlerSlots.get(listener.getOrder()).contains(listener)) {
			handlers = null;
			handlerSlots.get(listener.getOrder()).remove(listener);
		}
	}

	public void unregister(Object plugin) {
		boolean changed = false;
		for (List<ListenerRegistration> list : handlerSlots.values()) {
			for (ListIterator<ListenerRegistration> i = list.listIterator(); i.hasNext();) {
				if (i.next().getOwner().equals(plugin)) {
					i.remove();
					changed = true;
				}
			}
		}
		if (changed) {
			handlers = null;
		}
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 * @return The baked array of ListenerRegistrations
	 */
	public ListenerRegistration[] bake() {
		ListenerRegistration[] handlers = this.handlers;
		if (handlers != null) {
			return handlers; // don't re-bake when still valid
		}
		List<ListenerRegistration> entries = new ArrayList<ListenerRegistration>();
		for (Entry<Order, List<ListenerRegistration>> entry : handlerSlots.entrySet()) {
			entries.addAll(entry.getValue());
		}
		this.handlers = handlers = entries.toArray(new ListenerRegistration[entries.size()]);
		return handlers;
	}

	/**
	 * Gets an array of all currently ListenerRegistration, if the handlers list is currently null, it will attempt to bake new listeners prior to returning.
	 * @return array of ListenerRegistrations
	 */
	public ListenerRegistration[] getRegisteredListeners() {
		ListenerRegistration[] handlers = this.handlers;
		if (handlers == null) {
			handlers = bake();
		}
		return handlers;
	}
}
