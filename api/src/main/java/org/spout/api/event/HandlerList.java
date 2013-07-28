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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A list of event handlers, stored per-event.
 */
public final class HandlerList {
	/**
	 * Handler array. This field being an array is the key to this system's speed.
	 */
	private ListenerRegistration[] handlers = null;
	/**
	 * Returns the Dynamic handler lists. <br/> These are changed using register() and unregister()<br/> Changes automatically baked to the handlers array any time they have changed..
	 */
	private final EnumMap<Order, List<ListenerRegistration>> handlerSlots;
	private final CopyOnWriteArrayList<HandlerList> children = new CopyOnWriteArrayList<>(); // Not modified that much, it's fine
	private final HandlerList parent;
	/**
	 * List of all HandlerLists which have been created, for use in bakeAll()
	 */
	private static final ArrayList<HandlerList> ALL_LISTS = new ArrayList<>();

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
	 * Create a new handler list and initialize using EventPriority The HandlerList is then added to meta-list for use in bakeAll()
	 */
	public HandlerList() {
		this(null);
	}

	public HandlerList(HandlerList parent) {
		handlerSlots = new EnumMap<>(Order.class);
		for (Order o : Order.values()) {
			handlerSlots.put(o, new ArrayList<ListenerRegistration>());
		}
		ALL_LISTS.add(this);

		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
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
		dirty();
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
			dirty();
			handlerSlots.get(listener.getOrder()).remove(listener);
		}
	}

	public void unregister(Object plugin) {
		boolean changed = false;
		for (List<ListenerRegistration> list : handlerSlots.values()) {
			for (ListIterator<ListenerRegistration> i = list.listIterator(); i.hasNext(); ) {
				if (i.next().getOwner().equals(plugin)) {
					i.remove();
					changed = true;
				}
			}
		}
		if (changed) {
			dirty();
		}
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 *
	 * @return The baked array of ListenerRegistrations
	 */
	public ListenerRegistration[] bake() {
		ListenerRegistration[] handlers = this.handlers;
		if (handlers != null) {
			return handlers; // don't re-bake when still valid
		}
		List<ListenerRegistration> entries = new ArrayList<>();
		for (Order order : Order.values()) {
			addToEntries(entries, order);
		}
		this.handlers = handlers = entries.toArray(new ListenerRegistration[entries.size()]);
		return handlers;
	}

	private void dirty() {
		this.handlers = null;
		if (children.size() > 0) {
			for (int i = 0; i < children.size(); ++i) {
				children.get(i).dirty();
			}
		}
	}

	private void addToEntries(List<ListenerRegistration> entries, Order order) {
		List<ListenerRegistration> entry = handlerSlots.get(order);
		if (entry != null) {
			entries.addAll(entry);
		}

		if (parent != null) {
			parent.addToEntries(entries, order);
		}
	}

	/**
	 * Gets an array of all currently ListenerRegistration, if the handlers list is currently null, it will attempt to bake new listeners prior to returning.
	 *
	 * @return array of ListenerRegistrations
	 */
	public ListenerRegistration[] getRegisteredListeners() {
		ListenerRegistration[] handlers = this.handlers;
		if (handlers == null) {
			handlers = bake();
		}
		return handlers;
	}

	protected void addChild(HandlerList handlerList) {
		children.add(handlerList);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}
