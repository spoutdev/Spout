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
package org.spout.api.math;

import org.spout.api.util.concurrent.OptimisticReadWriteLock;

public class AtomicVector3 extends Vector3m {
	
	private final OptimisticReadWriteLock lock;
	
	public AtomicVector3(OptimisticReadWriteLock lock) {
		super();
		this.lock = handleNull(lock);
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

	@Override
	public void set(Vector3 vector) {
		int seq = lock.writeLock();
		try {
			super.set(vector);
		} finally {
			lock.writeUnlock(seq);
		}
	}
	
	/**
	 * Sets the value of the Vector3 without any synchronisation
	 * 
	 * @param vector
	 */
	public void directSet(Vector3 vector) {
		super.set(vector);
	}
	
	/**
	 * Sets the value of the Vector3 without any synchronisation
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void directSet(float x, float y, float z) {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
	}

	/**
	 * Atomically sets this point to the given values.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(float x, float y, float z) {
		int seq = lock.writeLock();
		try {
			super.setX(x);
			super.setY(y);
			super.setZ(z);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets this point to the given point.
	 * 
	 * @param point
	 */
	public void set(AtomicVector3 vector) {
		while (true) {
			int seq = vector.getLock().readLock();
			float x = 0;
			float y = 0;
			float z = 0;
			try {
				x = vector.getX();
				y = vector.getY();
				z = vector.getZ();
			} finally {
				if (vector.getLock().readUnlock(seq)) {
					set(x, y, z);
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
		}	}

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
}
