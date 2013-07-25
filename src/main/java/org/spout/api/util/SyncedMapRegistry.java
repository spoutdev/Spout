/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spout.api.event.object.EventableBase;

/**
 * Represents a map for mapping Strings to unique ids.
 *
 * The class supports conversion of ids between maps and allocation of new
 * unique ids for unknown Strings
 *
 * Conversions to and from parent/child maps are cached
 */
public final class SyncedMapRegistry extends EventableBase<SyncedMapEvent> {
	public static final byte REGISTRATION_MAP = -1;
	protected static final SyncedStringMap STRING_MAP_REGISTRATION = new SyncedStringMap("REGISTRATION_MAP"); // This is a special case
	protected static final ConcurrentMap<String, WeakReference<SyncedStringMap>> REGISTERED_MAPS = new ConcurrentHashMap<String, WeakReference<SyncedStringMap>>();

	public static SyncedStringMap get(String name) {
		WeakReference<SyncedStringMap> ref =  REGISTERED_MAPS.get(name);
		if (ref != null) {
			SyncedStringMap map = ref.get();
			if (map == null) {
				REGISTERED_MAPS.remove(name);
			}
			return map;
		}
		return null;
	}

	public static SyncedStringMap get(int id) {
		if (id == REGISTRATION_MAP) {
			return STRING_MAP_REGISTRATION;
		}
		String name = STRING_MAP_REGISTRATION.getString(id);
		if (name != null) {
			WeakReference<SyncedStringMap> ref =  REGISTERED_MAPS.get(name);
			if (ref != null) {
				SyncedStringMap map = ref.get();
				if (map == null) {
					REGISTERED_MAPS.remove(name);
				}
				return map;
			}
		}
		return null;
	}

	public static Collection<SyncedStringMap> getAll() {
		Collection<WeakReference<SyncedStringMap>> rawMaps = REGISTERED_MAPS.values();
		List<SyncedStringMap> maps = new ArrayList<SyncedStringMap>(rawMaps.size());
		for (WeakReference<SyncedStringMap> ref : rawMaps) {
			SyncedStringMap map = ref.get();
			if (map != null) {
				maps.add(map);
			}
		}
		return maps;
	}
	
	public static SyncedStringMap getRegistrationMap() {
		return STRING_MAP_REGISTRATION;
	}
	
	public static int register(SyncedStringMap map) {
		int id = STRING_MAP_REGISTRATION.register(map.getName());
		REGISTERED_MAPS.put(map.getName(), new WeakReference<SyncedStringMap>(map));
		return id;
	}
}
