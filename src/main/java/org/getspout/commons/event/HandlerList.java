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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map.Entry;

import java.util.*;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
@SuppressWarnings("unchecked")
public class HandlerList {
	/**
	 * Handler array. This field being an array is the key to this system's speed.
	 */
	private ListenerRegistration[][] handlers = new ListenerRegistration[Order.values().length][];

	/**
	 * Dynamic handler lists. These are changed using register() and
	 * unregister() and are automatically baked to the handlers array any
	 * time they have changed.
	 */
	private final EnumMap<Order, ArrayList<ListenerRegistration>> handlerslots;

	/**
	 * Whether the current HandlerList has been fully baked. When this is set
	 * to false, the Map<Order, List<RegisteredListener>> will be baked to RegisteredListener[][]
	 * next time the event is called.
	 *
	 * @see
	 */
	private boolean baked = false;

	/**
	 * List of all HandlerLists which have been created, for use in bakeAll()
	 */
	private static ArrayList<HandlerList> alllists = new ArrayList<HandlerList>();

	/**
	 * Bake all handler lists. Best used just after all normal event
	 * registration is complete, ie just after all plugins are loaded if
	 * you're using fevents in a plugin system.
	 */
	public static void bakeAll() {
		for (HandlerList h : alllists) {
			h.bake();
		}
	}

	public static void unregisterAll() {
		for (HandlerList h : alllists) {
			h.handlerslots.clear();
			h.baked = false;
		}
	}

	public static void unregisterAll(Object plugin) {
		for (HandlerList h : alllists) {
			h.unregister(plugin);
		}
	}

	/**
	 * Create a new handler list and initialize using EventPriority
	 * The HandlerList is then added to meta-list for use in bakeAll()
	 */
	public HandlerList() {
		handlerslots = new EnumMap<Order, ArrayList<ListenerRegistration>>(Order.class);
		for (Order o : Order.values()) {
			handlerslots.put(o, new ArrayList<ListenerRegistration>());
		}
		alllists.add(this);
	}

	/**
	 * Register a new listener in this handler list
	 *
	 * @param listener listener to register
	 */
	public void register(ListenerRegistration listener) {
		if (handlerslots.get(listener.getOrder()).contains(listener))
			throw new IllegalStateException("This listener is already registered to priority " + listener.getOrder().toString());
		baked = false;
		handlerslots.get(listener.getOrder()).add(listener);
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
		if (handlerslots.get(listener.getOrder()).contains(listener)) {
			baked = false;
			handlerslots.get(listener.getOrder()).remove(listener);
		}
	}

	public void unregister(Object plugin) {
		boolean changed = false;
		for (List<ListenerRegistration> list : handlerslots.values()) {
			for (ListIterator<ListenerRegistration> i = list.listIterator(); i.hasNext(); ) {
				if (i.next().getOwner().equals(plugin)) {
					i.remove();
					changed = true;
				}
			}
		}
		if (changed) baked = false;
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 */
	public void bake() {
		if (baked) return; // don't re-bake when still valid
		for (Entry<Order, ArrayList<ListenerRegistration>> entry : handlerslots.entrySet()) {
			handlers[entry.getKey().getIndex()] = (entry.getValue().toArray(new ListenerRegistration[entry.getValue().size()]));
		}
		baked = true;
	}

	public ListenerRegistration[][] getRegisteredListeners() {
		return handlers;
	}
}