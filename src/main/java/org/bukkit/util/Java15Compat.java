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

package org.bukkit.util;

import java.lang.reflect.Array;

public class Java15Compat {
	@SuppressWarnings("unchecked")
	public static <T> T[] Arrays_copyOfRange(T[] original, int start, int end) {
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				int length = end - start;
				int copyLength = Math.min(length, original.length - start);
				Object[] copy = (Object[]) Array.newInstance(original.getClass().getComponentType(), length);

				System.arraycopy(original, start, copy, 0, copyLength);
				return (T[]) copy;
			}
			throw new IllegalArgumentException();
		}
		throw new ArrayIndexOutOfBoundsException();
	}
}
