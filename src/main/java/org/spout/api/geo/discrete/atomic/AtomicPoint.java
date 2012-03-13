/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.geo.discrete.atomic;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.math.Matrix;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector2m;
import org.spout.api.math.Vector3;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * Represents a mutable position in a World
 */
public class AtomicPoint extends Pointm {

	/**
	 * Note: This is a non-reentrant lock!
	 */
	private final OptimisticReadWriteLock lock;

	public AtomicPoint(OptimisticReadWriteLock lock) {
		super();
		this.lock = handleNull(lock);
	}

	/**
	 * Atomically sets the world for this Point
	 *
	 * @param world
	 */
	@Override
	public void setWorld(World world) {
		int seq = lock.writeLock();
		try {
			super.setWorld(world);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets the x coordinate for this Point
	 *
	 * @param x
	 */
	@Override
	public void setX(float x) {
		int seq = lock.writeLock();
		try {
			super.setX(x);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets the y coordinate for this point
	 *
	 * @param y
	 */
	@Override
	public void setY(float y) {
		int seq = lock.writeLock();
		try {
			super.setY(y);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets the z coordinate for this point
	 *
	 * @param z
	 */
	@Override
	public void setZ(float z) {
		int seq = lock.writeLock();
		try {
			super.setZ(z);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Sets the value of the Point without any synchronisation
	 *
	 * @param point
	 */
	public void directSet(Point point) {
		super.set(point);
	}

	/**
	 * Sets the value of the Point without any synchronisation
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void directSet(World world, float x, float y, float z) {
		super.setWorld(world);
		super.setX(x);
		super.setY(y);
		super.setZ(z);
	}

	@Override
	public void set(Point point) {
		int seq = lock.writeLock();
		try {
			super.set(point);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets this point to the given values.
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(World world, float x, float y, float z) {
		int seq = lock.writeLock();
		try {
			super.setX(x);
			super.setY(y);
			super.setZ(z);
			super.setWorld(world);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets this point to the given point.
	 *
	 * @param point
	 */
	public void set(AtomicPoint point) {
		while (true) {
			int seq = point.getLock().readLock();
			float x = 0;
			float y = 0;
			float z = 0;
			World world = null;
			try {
				x = point.getX();
				y = point.getY();
				z = point.getZ();
				world = point.getWorld();
			} finally {
				if (point.getLock().readUnlock(seq)) {
					set(world, x, y, z);
					return;
				}
			}
		}
	}

	/**
	 * Atomically gets the x coordinate of this Point
	 *
	 * @return
	 */
	@Override
	public float getX() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.getX();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	/**
	 * Atomically gets the y coordinate of this Point
	 *
	 * @return
	 */
	@Override
	public float getY() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.getY();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	/**
	 * Atomically gets the z coordinate of this Point
	 *
	 * @return
	 */
	@Override
	public float getZ() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.getZ();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	/**
	 * Atomically gets the world of this Point
	 *
	 * @return
	 */
	@Override
	public World getWorld() {
		while (true) {
			int seq = lock.readLock();
			World world = null;
			try {
				world = super.getWorld();
			} finally {
				if (lock.readUnlock(seq)) {
					return world;
				}
			}
		}
	}

	@Override
	public double getManhattanDistance(Point other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.getManhattanDistance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public double getSquaredDistance(Point other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.getSquaredDistance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public double getDistance(Point other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.getDistance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public double getManhattanDistance(AtomicPoint other) {
		while (true) {
			int seq = other.getLock().readLock();
			double result = 0;
			try {
				getManhattanDistance((Point) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public double getMaxDistance(Point other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.getMaxDistance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public double getMaxDistance(AtomicPoint other) {
		while (true) {
			int seq = other.getLock().readLock();
			double result = 0;
			try {
				result = getMaxDistance((Point) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public int hashCode() {
		while (true) {
			int seq = lock.readLock();
			int hash = 0;
			try {
				hash = super.hashCode();
			} finally {
				if (lock.readUnlock(seq)) {
					return hash;
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		while (true) {
			int seq = lock.readLock();
			boolean equals = false;
			try {
				equals = super.equals(obj);
			} finally {
				if (lock.readUnlock(seq)) {
					return equals;
				}
			}
		}
	}

	@Override
	public String toString() {
		while (true) {
			int seq = lock.readLock();
			String string = ".toString() error in AtomicPoint";
			try {
				string = super.toString();
			} finally {
				if (lock.readUnlock(seq)) {
					return string;
				}
			}
		}
	}

	private OptimisticReadWriteLock handleNull(OptimisticReadWriteLock lock) {
		if (lock == null) {
			return new OptimisticReadWriteLock();
		} else {
			return lock;
		}
	}

	protected OptimisticReadWriteLock getLock() {
		return lock;
	}

	// Overrides from Vector3

	@Override
	public float dot(Vector3 other) {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.dot(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 cross(Vector3 other) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.cross(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public Vector3 cross(AtomicVector3 other) {
		while (true) {
			int seq = other.getLock().readLock();
			Vector3 result = null;
			try {
				result = cross((Vector3) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Point add(Vector3 other) {
		while (true) {
			int seq = lock.readLock();
			Point result = null;
			try {
				result = super.add(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public Vector3 add(AtomicVector3 other) {
		while (true) {
			int seq = other.getLock().readLock();
			Vector3 result = null;
			try {
				result = add((Vector3) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 subtract(Vector3 other) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.subtract(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public Vector3 subtract(AtomicVector3 other) {
		while (true) {
			int seq = other.getLock().readLock();
			Vector3 result = null;
			try {
				result = subtract((Vector3) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 ceil() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.ceil();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 floor() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.floor();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 round() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.round();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 abs() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.abs();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 normalize() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.normalize();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector2 toVector2() {
		while (true) {
			int seq = lock.readLock();
			Vector2 result = null;
			try {
				result = super.toVector2();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector2m toVector2m() {
		while (true) {
			int seq = lock.readLock();
			Vector2m result = null;
			try {
				result = super.toVector2m();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public float[] toArray() {
		while (true) {
			int seq = lock.readLock();
			float[] result = null;
			try {
				result = super.toArray();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public double distance(Vector3 other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.distance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public double distance(AtomicVector3 other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = distance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public float lengthSquared() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.lengthSquared();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public float length() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.length();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public float fastLength() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.fastLength();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 pow(double pow) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.pow(pow);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(double scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(float scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(int scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(double x, double y, double z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(float x, float y, float z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 multiply(int x, int y, int z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.multiply(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(double scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(float scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(int scale) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(scale);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(double x, double y, double z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(float x, float y, float z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 divide(int x, int y, int z) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.divide(x, y, z);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 transform(Quaternion other) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.transform(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public Vector3 transform(AtomicQuaternion other) {
		while (true) {
			int seq = other.getLock().readLock();
			Vector3 result = null;
			try {
				result = transform((Quaternion) other);
			} finally {
				if (other.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Vector3 transform(Matrix other) {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.transform(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public int compareTo(Vector3 o) {
		while (true) {
			int seq = lock.readLock();
			int result = 0;
			try {
				result = super.compareTo(o);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public int compareTo(AtomicVector3 o) {
		while (true) {
			int seq = o.getLock().readLock();
			int result = 0;
			try {
				result = compareTo((Vector3) o);
			} finally {
				if (o.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public int compareTo(AtomicPoint o) {
		while (true) {
			int seq = o.getLock().readLock();
			int result = 0;
			try {
				result = compareTo((Vector3) o);
			} finally {
				if (o.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}
	
	@Override
	public AtomicPoint clone() {
		AtomicPoint p = new AtomicPoint(new OptimisticReadWriteLock());
		p.set(this);
		return p;
	}
}
