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
package org.getspout.api.util;

public class MutableIntegerVector extends MutableVector {

	public MutableIntegerVector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getIntX() {
		return (int) x;
	}

	public int getIntY() {
		return (int) y;
	}

	public int getIntZ() {
		return (int) z;
	}

	public void setIntX(int x) {
		this.x = x;
	}

	public void setIntY(int y) {
		this.y = y;
	}

	public void setIntZ(int z) {
		this.z = z;
	}

	@Override
	public int hashCode() {
		int z = getIntZ();
		return getIntX() + (getIntY() << 24) + (z << 12) + (z >> 20);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MutableIntegerVector)) {
			return false;
		}
		MutableIntegerVector other = (MutableIntegerVector) o;

		return this.getIntX() == other.getIntX() && this.getIntY() == other.getIntY() && this.getIntZ() == other.getIntZ();

	}

}
