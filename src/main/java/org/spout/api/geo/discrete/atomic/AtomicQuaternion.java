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

import org.spout.api.math.Quaternion;
import org.spout.api.math.Quaternionm;
import org.spout.api.math.Vector3;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * Represents a rotation around a unit 4d circle.
 *
 *
 */
public class AtomicQuaternion extends Quaternionm {

	private final OptimisticReadWriteLock lock;

	/**
	 * Constructs a new Quaternion and sets the components equal to the identity
	 */
	public AtomicQuaternion(OptimisticReadWriteLock lock) {
		super();
		this.lock = handleNull(lock);
	}

	/**
	 * Constructs a new Quaternion with the given xyzw NOTE: This represents a
	 * Unit Vector in 4d space. Do not use unless you know what you are doing.
	 * If you want to create a normal rotation, use the angle/axis override.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public AtomicQuaternion(OptimisticReadWriteLock lock, float x, float y, float z, float w) {
		super(x, y, z, w);
		this.lock = handleNull(lock);
	}

	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbatrary axis
	 *
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param axis
	 */
	public AtomicQuaternion(OptimisticReadWriteLock lock, float angle, Vector3 axis) {
		super(angle, axis);
		this.lock = handleNull(lock);
	}

	/**
	 * Copy Constructor
	 */
	public AtomicQuaternion(OptimisticReadWriteLock lock, AtomicQuaternion rotation) {
		super(rotation);
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
	public void setW(float w) {
		int seq = lock.writeLock();
		try {
			super.setW(w);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	@Override
	public void set(Quaternion quaternion) {
		int seq = lock.writeLock();
		try {
			super.set(quaternion);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Sets the value of the quaternion without any synchronisation
	 *
	 * @param quaternion
	 */
	public void directSet(Quaternion quaternion) {
		super.set(quaternion);
	}

	/**
	 * Sets the value of the quaternion without any synchronisation
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void directSet(float x, float y, float z, float w) {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
		super.setW(w);
	}

	/**
	 * Atomically sets this quaternion to the given values.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void set(float x, float y, float z, float w) {
		int seq = lock.writeLock();
		try {
			super.setX(x);
			super.setY(y);
			super.setZ(z);
			super.setW(w);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	/**
	 * Atomically sets this point to the given point.
	 *
	 * @param point
	 */
	public void set(AtomicQuaternion quaternion) {
		while (true) {
			int seq = quaternion.getLock().readLock();
			float x = 0;
			float y = 0;
			float z = 0;
			float w = 0;
			try {
				x = quaternion.getX();
				y = quaternion.getY();
				z = quaternion.getZ();
				w = quaternion.getW();
			} finally {
				if (quaternion.getLock().readUnlock(seq)) {
					set(x, y, z, w);
					return;
				}
			}
		}
	}

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

	@Override
	public float getW() {
		while (true) {
			int seq = lock.readLock();
			float result = 0;
			try {
				result = super.getW();
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
	public Vector3 getAxisAngles() {
		while (true) {
			int seq = lock.readLock();
			Vector3 result = null;
			try {
				result = super.getAxisAngles();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public Quaternion rotate(float angle, Vector3 axis) {
		while (true) {
			int seq = lock.readLock();
			try {
				directSet(Quaternion.rotate(this, angle, axis));
			} finally {
				if (lock.readUnlock(seq)) {
					return this;
				}
			}
		}
	}

	public Quaternion rotate(float angle, AtomicVector3 axis) {
		while (true) {
			int seq = axis.getLock().readLock();
			try {
				directSet(Quaternion.rotate(this, angle, axis));
			} finally {
				if (axis.getLock().readUnlock(seq)) {
					return this;
				}
			}
		}
	}

	@Override
	public Quaternion normalize() {
		while (true) {
			int seq = lock.readLock();
			try {
				directSet(Quaternion.normalize(this));
			} finally {
				if (lock.readUnlock(seq)) {
					return this;
				}
			}
		}
	}

	@Override
	public Quaternion multiply(Quaternion o) {
		while (true) {
			int seq = lock.readLock();
			try {
				directSet(Quaternion.scale(this, o));
			} finally {
				if (lock.readUnlock(seq)) {
					return this;
				}
			}
		}
	}

	public Quaternion multiply(AtomicQuaternion o) {
		while (true) {
			int seq = o.getLock().readLock();
			try {
				directSet(Quaternion.scale(this, o));
			} finally {
				if (o.getLock().readUnlock(seq)) {
					return this;
				}
			}
		}
	}

	@Override
	public String toString() {
		while (true) {
			int seq = lock.readLock();
			String result = null;
			try {
				result = super.toString();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		while (true) {
			int seq = lock.readLock();
			boolean result = false;
			try {
				result = super.equals(o);
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	public boolean equals(AtomicQuaternion o) {
		while (true) {
			int seq = o.getLock().readLock();
			boolean result = false;
			try {
				result = equals((Object) o);
			} finally {
				if (o.getLock().readUnlock(seq)) {
					return result;
				}
			}
		}
	}

	@Override
	// TODO - should this even support hash code?
	public int hashCode() {
		while (true) {
			int seq = lock.readLock();
			int result = 0;
			try {
				result = super.hashCode();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}
	
	@Override
	public AtomicQuaternion clone() {
		AtomicQuaternion q = new AtomicQuaternion(new OptimisticReadWriteLock());
		q.set(this);
		return q;
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
}
