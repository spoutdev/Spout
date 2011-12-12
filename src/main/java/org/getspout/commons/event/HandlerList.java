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

import org.getspout.commons.addon.Addon;
import org.getspout.commons.addon.IllegalAddonAccessException;
import org.getspout.commons.event.Event;
import org.getspout.commons.event.HandlerList;
import org.getspout.commons.event.Listener;
import org.getspout.commons.event.ListenerRegistration;
import org.getspout.commons.event.Order;

/**
 * @author lahwran
 * @param <TEvent> Event type
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HandlerList<TEvent extends Event<TEvent>> {
	/**
	 * handler array. this field being an array is the key to this system's speed.
	 * 
	 * is initialized in bake().
	 */
	public Listener<TEvent>[][] handlers;

	/**
	 * Int array same length as handlers. each value in this array is the index of an Order slot, corossponding to the equivalent value in handlers.
	 * 
	 * is initialized in bake().
	 */
	public int[] handlerids;

	/**
	 * Dynamic handler lists. These are changed using register() and unregister() and are automatically baked to the handlers array any time they have changed.
	 */
	private final EnumMap<Order, ArrayList<ListenerRegistration<TEvent>>> handlerslots;

	/**
	 * Whether the current handlerslist has been fully baked. When this is set to false, the Map<Order, List<Listener>> will be baked to Listener[][] next time the event is called.
	 * 
	 * @see EventManager.callEvent
	 */
	private boolean baked = false;

	/**
	 * List of all handlerlists which have been created, for use in bakeall()
	 */
	private static ArrayList<HandlerList> alllists = new ArrayList<HandlerList>();

	/**
	 * Bake all handler lists. Best used just after all normal event registration is complete, ie just after all plugins are loaded if you're using fevents in a plugin system.
	 */
	public static void bakeall() {
		for (HandlerList h : alllists) {
			h.bake();
		}
	}

	public static void purgePlugin(Addon addon) {
		for (HandlerList h : alllists) {
			h.unregister(addon);
		}
	}

	public static void clearAll() {
		for (HandlerList h : alllists) {
			h.clear();
		}
	}

	/**
	 * Create a new handler list and initialize using EventManager.Order handlerlist is then added to meta-list for use in bakeall()
	 */
	public HandlerList() {
		handlerslots = new EnumMap<Order, ArrayList<ListenerRegistration<TEvent>>>(Order.class);
		for (Order o : Order.values()) {
			handlerslots.put(o, new ArrayList<ListenerRegistration<TEvent>>());
		}
		alllists.add(this);
	}

	private boolean isRegistered(ListenerRegistration registration, Order orderslot) {
		for (ListenerRegistration other : handlerslots.get(orderslot)) {
			if (other.getListener() == registration.getListener() && other.getAddon() == registration.getAddon()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Register a new listener in this handler list
	 * 
	 * @param listener listener to register
	 * @param order order location at which to call provided listener
	 * @param addon Addon this listener belongs to
	 */
	public void register(Listener<TEvent> listener, Order order, Addon addon) {
		if (!addon.isEnabled()) {
			throw new IllegalAddonAccessException("Addon attempted to register a listener while not enabled");
		}
		ListenerRegistration registration = new ListenerRegistration(listener, order, addon);
		if (isRegistered(registration, order)) {
			throw new IllegalStateException("This listener is already registered to order " + order.toString());
		}

		handlerslots.get(order).add(registration);
		baked = false;
	}

	/**
	 * Remove a listener from all order slots
	 * 
	 * @param listener listener to purge
	 */
	public void unregister(Listener<TEvent> listener) {
		for (Order o : Order.values()) {
			unregister(listener, o);
		}
	}

	/**
	 * Remove a listener from a specific order slot
	 * 
	 * @param listener listener to remove
	 * @param order order from which to remove listener
	 */
	public void unregister(Listener<TEvent> listener, Order order) {
		for (ListenerRegistration registration : handlerslots.get(order)) {
			if (registration.getListener() == listener) {
				baked = false;
				handlerslots.get(order).remove(registration);
			}
		}
	}

	/**
	 * Remove a plugin from all order slots
	 * 
	 * @param addon plugin to remove
	 */
	public void unregister(Addon addon) {
		for (Order o : Order.values()) {
			unregister(addon, o);
		}
	}

	/**
	 * Remove a plugin from a specific order slot
	 * 
	 * @param addon plugin to remove
	 * @param order order from which to remove plugin
	 */
	public void unregister(Addon addon, Order order) {
		for (ListenerRegistration registration : handlerslots.get(order)) {
			if (registration.getAddon() == addon) {
				baked = false;
				handlerslots.get(order).remove(registration);
			}
		}
	}

	private void clear() {
		for (Entry<Order, ArrayList<ListenerRegistration<TEvent>>> entry : handlerslots.entrySet()) {
			entry.getValue().clear();
		}
		baked = false;
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 */
	public void bake() {
		if (baked)
			return; // don't re-bake when still valid

		ArrayList<Listener[]> handlerslist = new ArrayList<Listener[]>();
		ArrayList<Integer> handleridslist = new ArrayList<Integer>();
		for (Entry<Order, ArrayList<ListenerRegistration<TEvent>>> entry : handlerslots.entrySet()) {
			Order orderslot = entry.getKey();

			ArrayList<ListenerRegistration<TEvent>> list = entry.getValue();

			int ord = orderslot.getIndex();
			Listener[] array = new Listener[list.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = list.get(i).getListener();
			}
			handlerslist.add(array);
			handleridslist.add(ord);
		}
		handlers = handlerslist.toArray(new Listener[handlerslist.size()][]);
		handlerids = new int[handleridslist.size()];
		for (int i = 0; i < handleridslist.size(); i++) {
			handlerids[i] = handleridslist.get(i);
		}
		baked = true;
	}
}
