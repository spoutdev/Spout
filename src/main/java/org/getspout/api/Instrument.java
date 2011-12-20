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

public enum Instrument {

	PIANO((byte) 0x0), // All other
	BASS_DRUM((byte) 0x1), // Stone
	SNARE_DRUM((byte) 0x2), // Sand
	STICKS((byte) 0x3), // Glass
	BASS_GUITAR((byte) 0x4); // Wood

	private final byte type;
	private final static Map<Byte, Instrument> types = new HashMap<Byte, Instrument>();

	private Instrument(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static Instrument getByType(final byte type) {
		return types.get(type);
	}

	static {
		for (Instrument instrument : Instrument.values()) {
			types.put(instrument.getType(), instrument);
		}
	}
}