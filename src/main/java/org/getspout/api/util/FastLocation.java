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

// TODO I (raphfrk) think I wrote this, need to check - anyway can be redone
//No worries, I wrote it based on your work - Afforess

import org.getspout.api.geo.World;
import org.getspout.api.block.Block;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

public class FastLocation extends Location {
	private final double yaw;
	private final double pitch;
	private final World world;

	public FastLocation(int x, int y, int z, double yaw, double pitch, World world) {
		super(world, x, y, z, yaw, pitch);
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

	public Vector3 getDirection() {
		Vector3m vector = new Vector3m(0,0,0);

		double rotX = this.getYaw();
		double rotY = this.getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double h = Math.cos(Math.toRadians(rotY));

		vector.setX(-h * Math.sin(Math.toRadians(rotX)));
		vector.setZ(h * Math.cos(Math.toRadians(rotX)));

		return vector;
	}
	
	public Vector3 toVector() {
		return new Vector3m(x, y, z);
	}
	
	public Block getBlock() {
		return null; // world.getBlockAt(this);
	}

}
