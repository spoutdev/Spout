/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.geo.discrete;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;

/**
 * Represents a position in a World
 */
public class Point extends Vector3 {
	private static final long serialVersionUID = 1L;

	protected final World world;
	public static final Point invalid = new Point(null, 0, 0, 0);
	
	/**
	 * Hashcode caching
	 */
	private transient volatile boolean hashed = false;
	private transient volatile int hashcode = 0;

	public Point(Point point) {
		super(point);
		world = point.getWorld();
	}

	public Point(Vector3 vector, World w) {
		super(vector);
		world = w;
	}

	public Point(World world, float x, float y, float z) {
		super(x, y, z);
		this.world = world;
	}

	@Override
	public Point divide(int val) {
		return new Point(super.divide(val), world);
	}

	@Override
	public Point divide(float val) {
		return new Point(super.divide(val), world);
	}

	@Override
	public Point divide(double val) {
		return new Point(super.divide(val), world);
	}
	
	@Override
	public Point divide(Vector3 other) {
		return new Point(super.divide(other), world);
	}

	@Override
	public Point divide(double x, double y, double z) {
		return new Point(super.divide(x, y, z), world);
	}

	@Override
	public Point divide(float x, float y, float z) {
		return new Point(super.divide(x, y, z), world);
	}

	@Override
	public Point divide(int x, int y, int z) {
		return new Point(super.divide(x, y, z), world);
	}

	@Override
	public Point multiply(int val) {
		return new Point(super.multiply(val), world);
	}

	@Override
	public Point multiply(float val) {
		return new Point(super.multiply(val), world);
	}

	@Override
	public Point multiply(double val) {
		return new Point(super.multiply(val), world);
	}
	
	@Override
	public Point multiply(Vector3 other) {
		return new Point(super.multiply(other), world);
	}

	@Override
	public Point multiply(double x, double y, double z) {
		return new Point(super.multiply(x, y, z), world);
	}

	@Override
	public Point multiply(float x, float y, float z) {
		return new Point(super.multiply(x, y, z), world);
	}

	@Override
	public Point multiply(int x, int y, int z) {
		return new Point(super.multiply(x, y, z), world);
	}

	public Point add(Point other) {
		if (world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		return new Point(MathHelper.add(this, other), world);
	}

	@Override
	public Point add(Vector3 other) {
		return new Point(super.add(other), world);
	}

	@Override
	public Point add(float x, float y, float z) {
		return new Point(super.add(x, y, z), world);
	}

	@Override
	public Point add(double x, double y, double z) {
		return new Point(super.add(x, y, z), world);
	}

	@Override
	public Point add(int x, int y, int z) {
		return new Point(super.add(x, y, z), world);
	}
	
	@Override
	public Point subtract(Vector3 other) {
		return new Point(super.subtract(other), world);
	}
	
	@Override
	public Point subtract(float x, float y, float z) {
		return new Point(super.subtract(x, y, z), world);
	}

	@Override
	public Point subtract(double x, double y, double z) {
		return new Point(super.subtract(x, y, z), world);
	}

	@Override
	public Point subtract(int x, int y, int z) {
		return new Point(super.subtract(x, y, z), world);
	}

	public int getBlockX() {
		return MathHelper.floor(this.getX());
	}

	public int getBlockY() {
		return MathHelper.floor(this.getY());
	}

	public int getBlockZ() {
		return MathHelper.floor(this.getZ());
	}

	/**
	 * Gets the square of the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getSquaredDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Gets the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getDistance(Point other) {
		return Math.sqrt(getSquaredDistance(other));
	}

	/**
	 * Gets the Manhattan distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getManhattanDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
	}

	/**
	 * Gets the largest distance between two points, when projected onto one of
	 * the axes.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the max distance.
	 */
	public double getMaxDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.max(Math.abs(x - other.x), Math.max(Math.abs(y - other.y), Math.abs(z - other.z)));
	}

	/**
	 * Gets the world this point is locate in
	 *
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(5033, 61).appendSuper(super.hashCode()).append(world).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		} else if (!(obj instanceof Point)) {
			return false;
		} else {
			Point point = (Point) obj;

			return point.world.equals(world) && point.x == x && point.y == y && point.z == z;
		}

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtil.toString(world, x, y, z);
	}
	
	public String toBlockString() {
		return "{" + world.getName() + ":" + getBlockX() + ", " + getBlockY() + "," + getBlockZ() + "}";
	}
	
	//Custom serialization logic because world can not be made serializable
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeFloat(this.x);
		out.writeFloat(this.y);
		out.writeFloat(this.z);
		out.writeUTF(world != null ? world.getName() : "null");
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		float x = in.readFloat();
		float y = in.readFloat();
		float z = in.readFloat();
		String world = in.readUTF();
		World w = Spout.getEngine().getWorld(world);
		try {
			Field field;
			
			field = Vector3.class.getDeclaredField("x");
			field.setAccessible(true);
			field.set(this, x);
			
			field = Vector3.class.getDeclaredField("y");
			field.setAccessible(true);
			field.set(this, y);
			
			field = Vector3.class.getDeclaredField("z");
			field.setAccessible(true);
			field.set(this, z);
			
			field = Point.class.getDeclaredField("world");
			field.setAccessible(true);
			field.set(this, w);
		}
		catch (Exception e) {
			if (Spout.debugMode()) {
				e.printStackTrace();
			}
		}
	}
}
