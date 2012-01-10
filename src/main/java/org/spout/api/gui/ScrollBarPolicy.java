/*
 * This file is part of Spout API (http://wiki.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import java.util.HashMap;

/**
 * When a Scrollable scrollbar will be shown.
 */
public enum ScrollBarPolicy {

	/**
	 * Shows the scrollbar when getMaximumScrollPosition is greater than 0
	 */
	SHOW_IF_NEEDED(0),
	/**
	 * Never show the scrollbar. However, you'll still be able to scroll with
	 * the scroll wheel or your trackpad or with arrow keys if the widget
	 * implemented that (like the list widget).
	 */
	SHOW_NEVER(1),
	/**
	 * Always show the scrollbar
	 */
	SHOW_ALWAYS(2);

	private final int id;
	private static HashMap<Integer, ScrollBarPolicy> ids = new HashMap<Integer, ScrollBarPolicy>();

	ScrollBarPolicy(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static ScrollBarPolicy getById(int id) {
		return ids.get(id);
	}

	static {
		for (ScrollBarPolicy s : values()) {
			ids.put(s.id, s);
		}
	}
}
