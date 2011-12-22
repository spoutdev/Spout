/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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
package org.getspout.unchecked.api.gui;

import java.util.HashMap;

/**
 * Types of animation, only one animation is permitted at a time, and note that
 * some types are limited to certain widget types...
 */
public enum WidgetAnim {

	/**
	 * No animation (default).
	 */
	NONE(0),
	/**
	 * Change the X by "value" pixels (any Widget).
	 */
	POS_X(1),
	/**
	 * Change the Y by "value" pixels (any Widget).
	 */
	POS_Y(2),
	/**
	 * Change the Width by "value" pixels (any Widget).
	 */
	WIDTH(3),
	/**
	 * Change the Height by "value" pixels (any Widget).
	 */
	HEIGHT(4),
	/**
	 * Change the Left offset by "value" pixels (Texture only).
	 */
	OFFSET_LEFT(5),
	/**
	 * Change the Top offset by "value" pixels (Texture only).
	 */
	OFFSET_TOP(6);
	private final int id;

	WidgetAnim(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private static final HashMap<Integer, WidgetAnim> lookupId = new HashMap<Integer, WidgetAnim>();

	static {
		for (WidgetAnim t : values()) {
			lookupId.put(t.getId(), t);
		}
	}

	public static WidgetAnim getAnimationFromId(int id) {
		return lookupId.get(id);
	}

	public boolean check(Widget widget) {
		switch (this) {
			case OFFSET_TOP:
			case OFFSET_LEFT:
				//				if (widget instanceof Texture) {
				//					return true;
				//				}
				return false;
		}
		return true;
	}
}
