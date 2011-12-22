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
package org.getspout.unchecked.api.util;

// Bukkit code - yes, this one is heavily cut/paste from bukkit. (Afforess)
// TODO need to fix

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.block.Block;

import org.getspout.api.geo.World;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

public class Location extends Vector3m {
	private double pitch;
	private double yaw;
	private World world;

	public Location() {
		this(null, 0, 0, 0, 0, 0);

	}

	/**
	 * Constructs a new Location with the given coordinates
	 *
	 * @param world The world in which this location resides
	 * @param x The x-coordinate of this new location
	 * @param y The y-coordinate of this new location
	 * @param z The z-coordinate of this new location
	 */
	public Location(final World world, final double x, final double y, final double z) {
		this(world, x, y, z, 0, 0);
	}

	/**
	 * Constructs a new Location with the given coordinates and direction
	 *
	 * @param world The world in which this location resides
	 * @param x The x-coordinate of this new location
	 * @param y The y-coordinate of this new location
	 * @param z The z-coordinate of this new location
	 * @param yaw The absolute rotation on the x-plane, in degrees
	 * @param pitch The absolute rotation on the y-plane, in degrees
	 */
	public Location(final World world, final double x, final double y, final double z, final double yaw, final double pitch) {
		super(x, y, z);
		this.world = world;
		this.pitch = pitch;
		this.yaw = yaw;
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

	public Location setYaw(double yaw) {
		this.yaw = yaw;
		return this;
	}

	public Location setPitch(double pitch) {
		this.pitch = pitch;
		return this;
	}

	public Location setWorld(World world) {
		this.world = world;
		return this;
	}

	public Vector3 getDirection() {
		Vector3m vector = new Vector3m(0, 0, 0);

		double rotX = getYaw();
		double rotY = getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double h = Math.cos(Math.toRadians(rotY));

		vector.setX(-h * Math.sin(Math.toRadians(rotX)));
		vector.setZ(h * Math.cos(Math.toRadians(rotX)));

		return vector;
	}

	public Block getBlock() {
		return null; // world.getBlockAt(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location other = (Location) obj;
			return new EqualsBuilder().append(getX(), other.getX()).append(getY(), other.getY()).append(getZ(), other.getZ()).append(getYaw(), other.getYaw()).append(getPitch(), other.getPitch()).append(getWorld(), other.getWorld()).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getX()).append(getY()).append(getZ()).append(getYaw()).append(getPitch()).append(getWorld()).toHashCode();
	}

	public Vector3 toVector() {
		return new Vector3m(x, y, z);
	}
}
