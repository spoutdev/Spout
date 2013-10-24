/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.geo.discrete;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.WorldSource;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.math.vector.Vector3f;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.util.StringUtil;

/**
 * Represents a position in a World
 */
public class Point extends Vector3f implements WorldSource {
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

	public Point(Vector3f vector, World w) {
		super(vector);
		world = w;
	}

	public Point(World world, float x, float y, float z) {
		super(x, y, z);
		this.world = world;
	}

	@Override
	public Point div(float val) {
		return new Point(super.div(val), world);
	}

	@Override
	public Point div(double val) {
		return new Point(super.div(val), world);
	}

	@Override
	public Point div(Vector3f other) {
		return new Point(super.div(other), world);
	}

	@Override
	public Point div(double x, double y, double z) {
		return new Point(super.div(x, y, z), world);
	}

	@Override
	public Point div(float x, float y, float z) {
		return new Point(super.div(x, y, z), world);
	}

	@Override
	public Point mul(float val) {
		return new Point(super.mul(val), world);
	}

	@Override
	public Point mul(double val) {
		return new Point(super.mul(val), world);
	}

	@Override
	public Point mul(Vector3f other) {
		return new Point(super.mul(other), world);
	}

	@Override
	public Point mul(double x, double y, double z) {
		return new Point(super.mul(x, y, z), world);
	}

	@Override
	public Point mul(float x, float y, float z) {
		return new Point(super.mul(x, y, z), world);
	}

	public Point add(Point other) {
		if (world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		return new Point(super.add(other), world);
	}

	@Override
	public Point add(Vector3f other) {
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
	public Point sub(Vector3f other) {
		return new Point(super.sub(other), world);
	}

	@Override
	public Point sub(float x, float y, float z) {
		return new Point(super.sub(x, y, z), world);
	}

	@Override
	public Point sub(double x, double y, double z) {
		return new Point(super.sub(x, y, z), world);
	}

	public int getBlockX() {
		return this.getFloorX();
	}

	public int getBlockY() {
		return this.getFloorY();
	}

	public int getBlockZ() {
		return this.getFloorZ();
	}

	public int getChunkX() {
		return this.getFloorX() >> Chunk.BLOCKS.BITS;
	}

	public int getChunkY() {
		return this.getFloorY() >> Chunk.BLOCKS.BITS;
	}

	public int getChunkZ() {
		return this.getFloorZ() >> Chunk.BLOCKS.BITS;
	}

	public Chunk getChunk(LoadOption loadopt) {
		return world.getChunk(getChunkX(), getChunkY(), getChunkZ(), loadopt);
	}

	public Region getRegion(LoadOption loadopt) {
		return world.getRegionFromChunk(getChunkX(), getChunkY(), getChunkZ(), loadopt);
	}

	/**
	 * Gets the square of the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getSquaredDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		double dx = getX() - other.getX();
		double dy = getY() - other.getY();
		double dz = getZ() - other.getZ();
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Gets the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getDistance(Point other) {
		return Math.sqrt(getSquaredDistance(other));
	}

	/**
	 * Gets the Manhattan distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 */
	public double getManhattanDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()) + Math.abs(getZ() - other.getZ());
	}

	/**
	 * Gets the largest distance between two points, when projected onto one of the axes.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the max distance.
	 */
	public double getMaxDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.max(Math.abs(getX() - other.getX()),
						Math.max(Math.abs(getY() - other.getY()),
						Math.abs(getZ() - other.getZ())));
	}

	/**
	 * Gets the world this point is locate in
	 *
	 * @return the world
	 */
	@Override
	public World getWorld() {
		return world;
	}

	/**
	 * Gets the block this point is locate in
	 *
	 * @return the world
	 */
	public Block getBlock() {
		return world.getBlock(getX(), getY(), getZ());
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
		if (!(obj instanceof Point)) {
			return false;
		} else {
			Point point = (Point) obj;
			boolean worldEqual = point.world == world || (point.world != null && point.world.equals(world));
			return worldEqual && point.getX() == getX() && point.getY() == getY() && point.getZ() == getZ();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtil.toString(world, getX(), getY(), getZ());
	}

	public String toBlockString() {
		return "{" + world.getName() + ":" + getBlockX() + ", " + getBlockY() + ", " + getBlockZ() + "}";
	}

	public String toChunkString() {
		return "{" + world.getName() + ":" + getChunkX() + ", " + getChunkY() + ", " + getChunkZ() + "}";
	}

	//Custom serialization logic because world can not be made serializable
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeFloat(this.getX());
		out.writeFloat(this.getY());
		out.writeFloat(this.getZ());
		out.writeUTF(world != null ? world.getName() : "null");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException {
		float x = in.readFloat();
		float y = in.readFloat();
		float z = in.readFloat();
		String world = in.readUTF();
		World w = Spout.getEngine().getWorld(world, true);
		try {
			Field field;

			field = Vector3f.class.getDeclaredField("x");
			field.setAccessible(true);
			field.set(this, x);

			field = Vector3f.class.getDeclaredField("y");
			field.setAccessible(true);
			field.set(this, y);

			field = Vector3f.class.getDeclaredField("z");
			field.setAccessible(true);
			field.set(this, z);

			field = Point.class.getDeclaredField("world");
			field.setAccessible(true);
			field.set(this, w);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			if (Spout.debugMode()) {
				e.printStackTrace();
			}
		}
	}
}
