/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.geo.cuboid;

/**\
 * Indicates fill orders for fill containers<br>
 * <br>
 * The intended usages is of the form<br>
 * <br>
 * int sourceIndex = 0;<br>
 * int targetIndex = 0;<br>
 * <br>
 * int thirdStep = targetFillOrder.thirdStep(source, sizeX, sizeY, sizeZ);<br>
 * int secondStep = targetFillOrder.secondStep(source, sizeX, sizeY, sizeZ);<br>
 * int firstStep = targetFillOrder.firstStep(source, sizeX, sizeY, sizeZ);<br>
 * <br>
 * int thirdMax = target.getThirdSize(sizeX, sizeY, sizeZ);<br>
 * int secondMax = target.getSecondSize(sizeX, sizeY, sizeZ);<br>
 * int firstMax = target.getFirstSize(sizeX, sizeY, sizeZ);<br>
 * <br>
 * <code>
 * &nbsp;for (int third = 0; third < thirdMax; third++) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int secondStart = sourceIndex;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;for (int second = 0; second < secondMax; second++) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int firstStart = sourceIndex;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for (int first = 0; first < firstMax; first++) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;target[targetIndex++] = source[sourceIndex += firstStep];<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sourceIndex = firstStart + secondStep;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;sourceIndex = secondStart + thirdStep;<br>
 * &nbsp;&nbsp;}
 * </code>
 */
public enum ContainerFillOrder {
	
	YXZ(Coord.Y, Coord.X, Coord.Z),
	YZX(Coord.Y, Coord.Z, Coord.X),
	ZXY(Coord.Z, Coord.X, Coord.Y),
	ZYX(Coord.Z, Coord.Y, Coord.X),
	XYZ(Coord.X, Coord.Y, Coord.Z),
	XZY(Coord.X, Coord.Z, Coord.Y);

	
	private final Coord first;
	private final Coord second;
	private final Coord third;
	
	ContainerFillOrder(Coord first, Coord second, Coord third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public int firstStep(ContainerFillOrder source, int sizeX, int sizeY, int sizeZ) {
		return step(source, first, sizeX, sizeY, sizeZ);
	}
	
	public int getFirstSize(int sizeX, int sizeY, int sizeZ) {
		return first.getSize(sizeX, sizeY, sizeZ);
	}
	
	public int secondStep(ContainerFillOrder source, int sizeX, int sizeY, int sizeZ) {
		return step(source, second, sizeX, sizeY, sizeZ);
	}
	
	public int getSecondSize(int sizeX, int sizeY, int sizeZ) {
		return second.getSize(sizeX, sizeY, sizeZ);
	}
	
	public int thirdStep(ContainerFillOrder source, int sizeX, int sizeY, int sizeZ) {
		return step(source, third, sizeX, sizeY, sizeZ);
	}
	
	public int getThirdSize(int sizeX, int sizeY, int sizeZ) {
		return third.getSize(sizeX, sizeY, sizeZ);
	}
	
	private int step(ContainerFillOrder source, Coord coord, int sizeX, int sizeY, int sizeZ) {
		if (coord == source.first) {
			return 1;
		} else if (coord == source.second) {
			return source.first.getSize(sizeX, sizeY, sizeZ);
		} else if (coord == source.third) {
			return source.first.getSize(sizeX, sizeY, sizeZ) * source.second.getSize(sizeX, sizeY, sizeZ);
		} else {
			throw new IllegalStateException("At least one coord must match");
		}
	}
	
	private enum Coord {
		X, Y, Z;
		
		public int getSize(int sizeX, int sizeY, int sizeZ) {
			switch (this) {
				case X: return sizeX;
				case Y: return sizeY;
				case Z: return sizeZ;
				default: throw new IllegalStateException("Unknown coord: " + this);
			}
		}

	}
}
