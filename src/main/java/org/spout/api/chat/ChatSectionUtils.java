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
package org.spout.api.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spout.api.chat.style.ChatStyle;

/**
 * Utility methods for implementations of {@link ChatSection}
 */
public class ChatSectionUtils {
	private ChatSectionUtils() {}

	/**
	 * Removes styles that conflict with {@code check} from the specified collection.
	 *
	 * @param collection The collection to check
	 * @param check The style to check conflicts against
	 */
	public static void removeConflicting(Collection<? extends ChatStyle> collection, ChatStyle check) {
		if (collection.size() > 0) {
			for (Iterator<? extends ChatStyle> i = collection.iterator(); i.hasNext();) {
				if (check.conflictsWith(i.next())) {
					i.remove();
				}
			}
		}
	}

	/**
	 * Gets a list from the given map at {@code key}, or creates one and adds it if there is no value
	 *
	 * @param map The map to source from
	 * @param key The key to look up
	 * @param <T> The type of list values
	 * @return The requested list
	 */
	public static <K, T> List<T> getOrCreateList(Map<K, List<T>> map, K key) {
		List<T> list = map.get(key);
		if (list == null) {
			list = new ArrayList<T>();
			map.put(key, list);
		}
		return list;
	}
}
