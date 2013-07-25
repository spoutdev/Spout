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
package org.spout.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.tuple.Pair;

import org.spout.api.event.object.Eventable;
import org.spout.api.event.object.EventableListener;
import org.spout.api.io.store.simple.SimpleStore;

/**
 * This class syncs a StringToUniqueIntegerMap from server to client
 */
public final class SyncedStringMap extends StringToUniqueIntegerMap implements Eventable<SyncedMapEvent> {
	private final CopyOnWriteArrayList<EventableListener<SyncedMapEvent>> registeredListeners = new CopyOnWriteArrayList<EventableListener<SyncedMapEvent>>();
	private int id;

	protected SyncedStringMap(String name) {
		super(name);
	}

	public SyncedStringMap(StringToUniqueIntegerMap parent, SimpleStore<Integer> store, int minId, int maxId, String name) {
		super(parent, store, minId, maxId, name);
	}

	public static SyncedStringMap create(String name) {
		SyncedStringMap map = new SyncedStringMap(name);
		map.id = SyncedMapRegistry.register(map);
		return map;
	}

	public static SyncedStringMap create(StringToUniqueIntegerMap parent, SimpleStore<Integer> store, int minId, int maxId, String name) {
		SyncedStringMap map = new SyncedStringMap(parent, store, minId, maxId, name);
		map.id = SyncedMapRegistry.register(map);
		return map;
	}

	@Override
	public int register(String key) {
		Integer id = store.get(key);
		if (id != null) {
			return id;
		}
		int local = super.register(key);
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.ADD, Arrays.asList(Pair.of(local, key))));
		return local;
	}

	@Override
	public boolean register(String key, int id) {
		Integer local = store.get(key);
		if (local != null) {
			return false;
		}
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.ADD, Arrays.asList(Pair.of(id, key))));
		return super.register(key, id);
	}

	public void handleUpdate(SyncedMapEvent message) {
		switch (message.getAction()) {
			case SET:
				clear();
			case ADD:
				for (Pair<Integer, String> pair : message.getModifiedElements()) {
					store.set(pair.getValue(), pair.getKey());
				}
				break;
			case REMOVE:
				for (Pair<Integer, String> pair : message.getModifiedElements()) {
					store.remove(pair.getValue());
				}
				break;
		}
	}

	@Override
	public void clear() {
		super.clear();
		callEvent(new SyncedMapEvent(this, SyncedMapEvent.Action.SET, new ArrayList<Pair<Integer, String>>()));
	}

	public int getId() {
		return id;
	}

	@Override
	public void registerListener(EventableListener<SyncedMapEvent> listener) {
		registeredListeners.add(listener);
	}

	@Override
	public void unregisterAllListeners() {
		registeredListeners.clear();
	}

	@Override
	public void unregisterListener(EventableListener<SyncedMapEvent> listener) {
		registeredListeners.remove(listener);
	}

	@Override
	public void callEvent(SyncedMapEvent event) {
		for (EventableListener<SyncedMapEvent> listener : registeredListeners) {
			listener.onEvent(event);
		}
	}
}
