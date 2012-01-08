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
package org.spout.server.util;

/**
 * Stores a 3 int triple.
 *
 * Objects of this type can be used in HashMaps.
 *
 * TripleInt.NULL can be added to HashMaps that don't support null objects/
 */
public class TripleInt {

	public final static TripleInt NULL = new TripleInt(0, 0, 0);

	public final int x;
	public final int y;
	public final int z;

	public TripleInt(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		int hash = x;
		hash += (hash << 11) + y;
		hash += (hash << 9) + z;
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TripleInt)) {
			return false;
		} else {
			TripleInt other = (TripleInt) o;

			if (other == NULL) {
				return this == NULL;
			} else {
				return other.x == x && other.y == y && other.z == z;
			}
		}
	}

}
