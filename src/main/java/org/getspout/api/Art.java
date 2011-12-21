/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api;

import java.util.HashMap;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents the painting on a {@link Painting}.
 */
public enum Art {
	KEBAB(0, 1, 1),
	AZTEC(1, 1, 1),
	ALBAN(2, 1, 1),
	AZTEC2(3, 1, 1),
	BOMB(4, 1, 1),
	PLANT(5, 1, 1),
	WASTELAND(6, 1, 1),
	WANDERER(7, 1, 2),
	GRAHAM(8, 1, 2),
	POOL(9, 2, 1),
	COURBET(10, 2, 1),
	SUNSET(11, 2, 1),
	SEA(12, 2, 1),
	CREEBET(13, 2, 1),
	MATCH(14, 4, 2),
	BUST(15, 2, 2),
	STAGE(16, 2, 2),
	VOID(17, 2, 2),
	SKULL_AND_ROSES(18, 2, 2),
	FIGHTERS(19, 2, 2),
	SKELETON(20, 4, 3),
	DONKEYKONG(21, 4, 3),
	POINTER(22, 4, 4),
	PIGSCENE(23, 4, 4),
	BURNINGSKULL(24, 4, 4), ;
	private final int id;
	private final int width;
	private final int height;
	private static HashMap<String, Art> names = new HashMap<String, Art>();
	private static TIntObjectHashMap<Art> ids = new TIntObjectHashMap<Art>();
	static {
		for (Art art : Art.values()) {
			ids.put(art.id, art);
			names.put(art.name(), art);
		}
	}

	private Art(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	/**
	 * Gets the width of the painting
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the painting
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the painting id
	 *
	 * @return painting id
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		String name = name();
		String start = "" + name.charAt(0);
		return start.toUpperCase() + name.substring(1);
	}

	/**
	 * Search for a painting by it's id.
	 *
	 * @param id painting id
	 * @return painting if found, else null
	 */
	public static Art getById(int id) {
		return ids.get(id);
	}

	/**
	 * Searches for a painting based on it's enum {@link #name()}.
	 *
	 * @param name to search for
	 * @return painting if found, else null
	 */
	public static Art getByName(String name) {
		return names.get(name);
	}
}
