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

import org.getspout.api.World;
import org.getspout.api.block.Block;

public class FastLocation extends FastVector implements FixedLocation {
	private final double yaw;
	private final double pitch;
	private final World world;

	public FastLocation(int x, int y, int z, double yaw, double pitch, World world) {
		super(x, y, z);
		this.yaw = yaw;
		this.pitch = pitch;
		this.world = world;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public World getWorld() {
		return world;
	}

	public Vector getDirection() {
		Vector vector = new MutableVector();

		double rotX = this.getYaw();
		double rotY = this.getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double h = Math.cos(Math.toRadians(rotY));

		vector.setX(-h * Math.sin(Math.toRadians(rotX)));
		vector.setZ(h * Math.cos(Math.toRadians(rotX)));

		return vector;
	}
	
	public Vector toVector() {
		return new MutableVector(x, y, z);
	}
	
	public Block getBlock() {
		return world.getBlockAt(this);
	}

}
