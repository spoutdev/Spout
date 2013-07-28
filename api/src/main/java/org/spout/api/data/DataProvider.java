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
package org.spout.api.data;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.server.RetrieveDataEvent;

/**
 * Collection of data to easily set data for {@link DataSubject}s. Must be registered as a {@link Listener}.
 */
public class DataProvider implements Listener {
	private final HashMap<DataSubject, Map<String, ValueHolder>> queue = new HashMap<>();

	public void set(DataSubject subject, String key, ValueHolder value) {
		// get the subjects data, create if null
		if (!queue.containsKey(subject)) {
			queue.put(subject, new HashMap<String, ValueHolder>());
		}
		Map<String, ValueHolder> data = queue.get(subject);
		data.put(key, value);
	}

	public void set(DataSubject subject, String key, Object value) {
		set(subject, key, new DataValue(value));
	}

	public void remove(DataSubject subject, String key) {
		if (!queue.containsKey(subject)) {
			queue.get(subject).remove(key);
		}
	}

	@EventHandler
	public void sendData(RetrieveDataEvent event) {
		// find a matching subject
		for (Map.Entry<DataSubject, Map<String, ValueHolder>> entry : queue.entrySet()) {
			if (event.getSubject().equals(entry.getKey())) {
				// find a matching node
				for (Map.Entry<String, ValueHolder> e : entry.getValue().entrySet()) {
					if (event.getNode().equalsIgnoreCase(e.getKey())) {
						// set the result
						event.setResult(e.getValue());
					}
				}
			}
		}
	}
}
