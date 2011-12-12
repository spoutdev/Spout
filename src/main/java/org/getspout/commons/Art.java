/*
 * This file is part of Bukkit (http://bukkit.org/).
 * 
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

package org.getspout.commons;

import java.util.HashMap;

/**
 * Represents the art on a painting
 */
public enum Art {
	KEBAB(0,1,1),
	AZTEC(1,1,1),
	ALBAN(2,1,1),
	AZTEC2(3,1,1),
	BOMB(4,1,1),
	PLANT(5,1,1),
	WASTELAND(6,1,1),
	POOL(7,2,1),
	COURBET(8,2,1),
	SEA(9,2,1),
	SUNSET(10,2,1),
	CREEBET(11,2,1),
	WANDERER(12,1,2),
	GRAHAM(13,1,2),
	MATCH(14,4,2),
	BUST(15,2,2),
	STAGE(16,2,2),
	VOID(17,2,2),
	SKULL_AND_ROSES(18,2,2),
	FIGHTERS(19,2,2),
	POINTER(20,4,4),
	PIGSCENE(21,4,4),
	BURNINGSKULL(22,4,4),
	SKELETON(23,4,3),
	DONKEYKONG(24,4,3);
	private int id, width, height;
	private static HashMap<String,Art> names = new HashMap<String,Art>();
	private static HashMap<Integer,Art> ids = new HashMap<Integer,Art>();
	static {
		for (Art art : Art.values()) {
			ids.put(art.id, art);
			names.put(art.toString(), art);
		}
	}

	private Art(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}

	/**
	 * Gets the width of the painting, in blocks
	 *
	 * @return The width of the painting, in blocks
	 */
	public int getBlockWidth() {
		return width;
	}

	/**
	 * Gets the height of the painting, in blocks
	 *
	 * @return The height of the painting, in blocks
	 */
	public int getBlockHeight() {
		return height;
	}

	/**
	 * Get the ID of this painting.
	 *
	 * @return The ID of this painting
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get a painting by its numeric ID
	 *
	 * @param id The ID
	 * @return The painting
	 */
	public static Art getById(int id) {
		return ids.get(id);
	}

	/**
	 * Get a painting by its unique name
	 *
	 * @param name The name
	 * @return The painting
	 */
	public static Art getByName(String name) {
		return names.get(name);
	}
}
