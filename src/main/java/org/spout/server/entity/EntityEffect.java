/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.entity;

import java.util.HashMap;
import java.util.Map;

public enum EntityEffect {
	MOVE_SPEED(1),
	MOVE_SLOW(2),
	DIG_SPEED(3),
	DIG_SLOW(4),
	DAMAGE_BOOST(5),
	HEAL(6),
	HARM(7),
	JUMP(8),
	CONFUSION(9),
	REGENERATION(10),
	RESISTANCE(11),
	FIRE_RESISTANCE(12),
	WATER_BREATHING(13),
	INVISIBILITY(14),
	BLINDNESS(15),
	NIGHT_VISION(16),
	HUNGER(17),
	WEAKNESS(18),
	POISON(19);

	private final byte id;

	private static final Map<Integer, EntityEffect> lookup = new HashMap<Integer, EntityEffect>();

	static {
		for (EntityEffect effect : EntityEffect.values()) {
			lookup.put((int) effect.getId(), effect);
		}
	}

	public static EntityEffect getById(int id) {
		return lookup.get(id);
	}

	private EntityEffect(int id) {
		this.id = (byte) id;
	}

	public byte getId() {
		return id;
	}
}