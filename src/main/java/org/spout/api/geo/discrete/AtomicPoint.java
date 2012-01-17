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
import org.spout.api.math.Quaternion;
import org.spout.api.util.concurrent.AtomicLinkable;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * Represents a mutable position in a World
 */
public class AtomicPoint extends Pointm implements AtomicLinkable {

	private final OptimisticReadWriteLock lock;

	public AtomicPoint(OptimisticReadWriteLock lock) {
		super();
		this.lock = getLock(lock);
	}

	@Override
	public void setWorld(World world) {
		int seq = lock.writeLock();
		try {
			super.setWorld(world);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	@Override
	public void setX(float x) {
		int seq = lock.writeLock();
		try {
			super.setX(x);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	@Override
	public void setY(float y) {
		int seq = lock.writeLock();
		try {
			super.setY(y);
		} finally {
			lock.writeUnlock(seq);
		}
	}

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
	 * @param quaternion
	 */
	public void directSet(Point point) {
		super.set(point);
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

	// Reads

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

	private OptimisticReadWriteLock getLock(OptimisticReadWriteLock lock) {
		if (lock == null) {
			return new OptimisticReadWriteLock();
		} else {
			return lock;
		}
	}

	public OptimisticReadWriteLock getLock() {
		return lock;
	}



}
