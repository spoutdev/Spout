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
package org.getspout.commons;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public enum ChatColor {
	BLACK(0),

	DARK_BLUE(1),

	DARK_GREEN(2),

	DARK_AQUA(3),

	DARK_RED(4),

	DARK_PURPLE(5),

	GOLD(6),

	GRAY(7),

	DARK_GRAY(8),

	BLUE(9),

	GREEN(10),

	AQUA(11),

	RED(12),

	LIGHT_PURPLE(13),

	YELLOW(14),

	WHITE(15);

	private final int code;
	private static final Map<Integer, ChatColor> colors;

	private ChatColor(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public String toString() {
		return String.format("\u00A7%x", new Object[] { Integer.valueOf(this.code) });
	}

	public static ChatColor getByCode(int code) {
		return (ChatColor) colors.get(Integer.valueOf(code));
	}

	public static String stripColor(String input) {
		if (input == null) {
			return null;
		}

		return input.replaceAll("(?i)\u00A7[0-F]", "");
	}

	static {
		colors = new HashMap();

		for (ChatColor color : values())
			colors.put(Integer.valueOf(color.getCode()), color);
	}
}
