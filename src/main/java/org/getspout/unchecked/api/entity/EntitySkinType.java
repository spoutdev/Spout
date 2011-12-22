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
package org.getspout.unchecked.api.entity;

public enum EntitySkinType {
	DEFAULT(0),
	SPIDER_EYES(1),
	SHEEP_FUR(2),
	WOLF_ANGRY(3),
	WOLF_TAMED(4),
	PIG_SADDLE(5),
	GHAST_MOUTH(6),
	ENDERMAN_EYES(7), ;

	private final byte id;

	private EntitySkinType(int id) {
		this.id = (byte) id;
	}

	public byte getId() {
		return id;
	}

	public static EntitySkinType getType(byte id) {
		for (EntitySkinType type : values()) {
			if (type.id == id) {
				return type;
			}
		}
		return null;
	}

}
