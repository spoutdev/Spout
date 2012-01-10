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
 * This is used to define the orientation for Scrollable widgets.
 */
public enum Orientation {
	/**
	 * Horizontal axis (left-right)
	 */
	HORIZONTAL(0),
	/**
	 * Vertical axis (top-bottom)
	 */
	VERTICAL(1);
	
	private final int id;
	Orientation(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private static final HashMap<Integer, Orientation> lookupId = new HashMap<Integer, Orientation>();

	static {
		for (Orientation t : values()) {
			lookupId.put(t.getId(), t);
		}
	}

	public static Orientation getOrientationFromId(int id) {
		return lookupId.get(id);
	}

	public Orientation getOther() {
		switch(this) {
		case HORIZONTAL:
			return VERTICAL;
		case VERTICAL:
			return HORIZONTAL;
		}
		return null;
	}
}
