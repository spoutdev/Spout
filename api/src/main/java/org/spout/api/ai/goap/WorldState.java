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
package org.spout.api.ai.goap;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class WorldState implements Cloneable {
	private final Map<String, Object> state;

	private WorldState() {
		state = Maps.newHashMap();
	}

	private WorldState(Map<String, Object> newState) {
		state = newState;
	}

	public WorldState apply(WorldState effects) {
		if (effects == EMPTY || effects.state.isEmpty())
			return this;
		Map<String, Object> newState = Maps.newHashMap(effects.state);
		for (Entry<String, Object> entry : state.entrySet()) {
			if (!newState.containsKey(entry.getKey()))
				newState.put(entry.getKey(), entry.getValue());
		}
		return new WorldState(newState);
	}

	@Override
	public WorldState clone() {
		try {
			return (WorldState) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean contains(WorldState o) {
		return difference(o) == 0;
	}

	public int difference(WorldState goal) {
		int differences = 0;
		for (Entry<String, Object> entry : goal.state.entrySet()) {
			Object value = state.get(entry.getKey());
			if (value == null) {
				continue;
			}
			if (!value.equals(entry.getValue())) {
				differences++;
				continue;
			}
		}
		return differences;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String string) {
		return (T) state.get(string);
	}

	public void put(String key, Object value) {
		state.put(key, value);
	}

	@Override
	public String toString() {
		return state.toString();
	}

	public static final WorldState EMPTY = WorldState.createEmptyState();

	private static WorldState create(Object[] objects) {
		return create(objects, false);
	}

	private static WorldState create(Object[] objects, boolean immutable) {
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < objects.length; i += 2) {
			map.put((String) objects[i], objects[i + 1]);
		}
		if (immutable)
			map = ImmutableMap.copyOf(map);
		return new WorldState(map);
	}

	public static WorldState create(String firstKey, Object firstValue) {
		return create(new Object[] { firstKey, firstValue });
	}

	public static WorldState create(String firstKey, Object firstValue, String secondKey, Object secondValue) {
		return create(new Object[] { firstKey, firstValue, secondKey, secondValue });
	}

	public static WorldState createEmptyState() {
		return new WorldState();
	}

	public static WorldState createImmutable(String firstKey, Object firstValue) {
		return create(new Object[] { firstKey, firstValue }, true);
	}

	public static WorldState createImmutable(String firstKey, Object firstValue, String secondKey, Object secondValue) {
		return create(new Object[] { firstKey, firstValue, secondKey, secondValue }, true);
	}
}
