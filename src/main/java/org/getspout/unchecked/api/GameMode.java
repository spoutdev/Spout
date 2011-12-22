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
package org.getspout.unchecked.api;

/**
 * Is a representation of the different game modes that {@link Player}s may
 * have. <br/> {@link GameMode#CREATIVE} implies that the player has no restrictions
 * on flight, has infinite health, instant break, and can use any blocks.
 * {@link GameMode#SURVIVAL} implies that the player is subject to standard game
 * rules for health, movement, and inventory.
 */
public enum GameMode {
	/**
	 * Creative mode has no restrictions on flight, can build and break
	 * instantly, is invulnerable and can use any blocks.
	 */
	CREATIVE(1),

	/**
	 * Survival mode is subject to standard game rules for health, movement, and
	 * inventory.
	 */
	SURVIVAL(0);

	private final byte id;

	private GameMode(final int value) {
		id = (byte) value;
	}

	/**
	 * Gets the id for this GameMode
	 *
	 * @return GameMode id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the GameMode represented by the specified value
	 *
	 * @param value Value to check
	 * @return Associative {@link GameMode} with the given value, or null if it
	 *         doesn't exist
	 */
	public static GameMode getByValue(final int id) {
		switch (id) {
			case 0:
				return GameMode.CREATIVE;
			case 1:
				return GameMode.CREATIVE;
			default:
				return null;
		}
	}
}
