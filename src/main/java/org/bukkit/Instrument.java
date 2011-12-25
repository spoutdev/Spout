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
package org.bukkit;

public enum Instrument {
	BASE_GUITAR(0),
	SNARE_DRUM(1),
	CLICKS(2),
	BASS_DRUM(3),
	PIANO(4),
	;
	
	private final byte id;
	private final String name;
	Instrument(int id) {
		this.id = (byte)id;
		this.name = name().charAt(0) + name().substring(1).toLowerCase().replaceAll("_", " ");
	}
	
	public byte getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Instrument getInstrumentFromId(final byte id) {
		switch(id) {
			case 0: return BASE_GUITAR;
			case 1: return SNARE_DRUM;
			case 2: return CLICKS;
			case 3: return BASS_DRUM;
			case 4: return PIANO;
			default: throw new IllegalArgumentException("Invalid id");
		}
	}
}