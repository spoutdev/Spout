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
package org.spout.api.geo.discrete;

import org.spout.api.geo.World;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * Represents a mutable position in a World
 */
public class AtomicPoint extends Pointm {

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
	public double getMahattanDistance(Point other) {
		while (true) {
			int seq = lock.readLock();
			double result = 0;
			try {
				result = super.getMahattanDistance(other);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}
	
	public double getMahattanDistance(AtomicPoint other) {
		while (true) {
			int seq = other.getLock().readLock();
			double result = 0;
			try {
				getMahattanDistance((Point)other);
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
				result = getMaxDistance((Point)other);
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

	private OptimisticReadWriteLock getLock() {
		return lock;
	}
	
	// Overrides for Vector3
}
