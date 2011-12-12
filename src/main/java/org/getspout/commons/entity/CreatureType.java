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
package org.getspout.commons.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CreatureType {
	CHICKEN("Chicken"),
	COW("Cow"),
	CREEPER("Creeper"),
	GHAST("Ghast"),
	GIANT("Giant"),
	MONSTER("Monster"),
	PIG("Pig"),
	PIG_ZOMBIE("PigZombie"),
	SHEEP("Sheep"),
	SKELETON("Skeleton"),
	SLIME("Slime"),
	SPIDER("Spider"),
	SQUID("Squid"),
	ZOMBIE("Zombie"),
	WOLF("Wolf"),
	CAVE_SPIDER("CaveSpider"),
	ENDERMAN("Enderman"),
	SILVERFISH("Silverfish");

	private String name;

	private static final Map<String, CreatureType> mapping = new HashMap<String, CreatureType>();

	static {
		for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
			mapping.put(type.name, type);
		}
	}

	private CreatureType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static CreatureType fromName(String name) {
		return mapping.get(name);
	}
}

