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
import java.util.Map;

public enum Statistic {
	DAMAGE_DEALT(2020),
	DAMAGE_TAKEN(2021),
	DEATHS(2022),
	MOB_KILLS(2023),
	PLAYER_KILLS(2024),
	FISH_CAUGHT(2025),
	MINE_BLOCK(16777216, true),
	USE_ITEM(6908288, false),
	BREAK_ITEM(16973824, true);

	private final static Map<Integer, Statistic> statistics = new HashMap<Integer, Statistic>();
	private final int id;
	private final boolean isSubstat;
	private final boolean isBlock;

	private Statistic(int id) {
		this(id, false, false);
	}

	private Statistic(int id, boolean isBlock) {
		this(id, true, isBlock);
	}

	private Statistic(int id, boolean isSubstat, boolean isBlock) {
		this.id = id;
		this.isSubstat = isSubstat;
		this.isBlock = isBlock;
	}

	public int getId() {
		return id;
	}

	public boolean isSubstatistic() {
		return isSubstat;
	}

	public boolean isBlock() {
		return isSubstat && isBlock;
	}

	public static Statistic getStatistic(int id) {
		return statistics.get(id);
	}

	static {
		for (Statistic stat : values()) {
			statistics.put(stat.getId(), stat);
		}
	}
}
