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
import java.util.Map;

public enum Achievement {
	OPEN_INVENTORY(0),
	MINE_WOOD(1),
	BUILD_WORKBENCH(2),
	BUILD_PICKAXE(3),
	BUILD_FURNACE(4),
	ACQUIRE_IRON(5),
	BUILD_HOE(6),
	MAKE_BREAD(7),
	BAKE_CAKE(8),
	BUILD_BETTER_PICKAXE(9),
	COOK_FISH(10),
	ON_A_RAIL(11),
	BUILD_SWORD(12),
	KILL_ENEMY(13),
	KILL_COW(14),
	FLY_PIG(15);

	/**
	 * The offset used to distinguish Achievements and Statistics
	 */
	public final static int STATISTIC_OFFSET = 5242880;
	private final static Map<Integer, Achievement> achievements = new HashMap<Integer, Achievement>();
	private final int id;

	private Achievement(int id) {
		this.id = STATISTIC_OFFSET + id;
	}

	/**
	 * Gets the ID for this achievement.
	 * 
	 * Note that this is offset using {@link #STATISTIC_OFFSET}
	 * 
	 * @return ID of this achievement
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the achievement associated with the given ID.
	 * 
	 * Note that the ID must already be offset using {@link #STATISTIC_OFFSET}
	 * 
	 * @param id ID of the achievement to return
	 * @return Achievement with the given ID
	 */
	public static Achievement getAchievement(int id) {
		return achievements.get(id);
	}

	static {
		for (Achievement ach : values()) {
			achievements.put(ach.getId(), ach);
		}
	}
}
