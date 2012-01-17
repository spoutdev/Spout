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

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.geo.World;
import org.spout.api.math.AtomicQuaternion;
import org.spout.api.math.AtomicVector3;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Quaternionm;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector3m;
import org.spout.api.util.concurrent.AtomicLinkable;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

public class Transform implements AtomicLinkable {
	private final OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private final AtomicPoint position = new AtomicPoint(lock);
	private final AtomicQuaternion rotation = new AtomicQuaternion(lock);
	private final AtomicVector3 scale = new AtomicVector3(lock);

	private final AtomicReference<Transform> parent = null;

	public Transform() {
	}

	public Transform(Point position, Quaternion rotation, Vector3 scale) {
		setPosition(position);
		setRotation(rotation);
		setScale(scale);
	}

	public Pointm getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position.set(position);
	}
	public Quaternionm getRotation() {
		return rotation;
	}
	public void setRotation(Quaternion rotation) {
		this.rotation.set(rotation);
	}
	public Vector3m getScale() {
		return scale;
	}
	public void setScale(Vector3 scale) {
		this.scale.set(scale);
	}
	public Transform getParent() {
		while (true) {
			int seq = lock.readLock();
			Transform result = null;
			try {
				result = parent.get();
			} finally {
				if (lock.readUnlock(seq)) {
					return result;
				}
			}
		}
	}
	
	public void setParent(Transform parent) {
		int seq = lock.writeLock();
		try {
			this.parent.set(parent);
		} finally {
			lock.writeUnlock(seq);
		}
	}

	public void set(Transform transform) {
		int seq = lock.writeLock();
		try {
			while (true) {
				int seq2 = transform.getLock().readLock();
				this.position.directSet(transform.getPosition());
				this.rotation.directSet(transform.getRotation());
				this.scale.directSet(transform.getScale());
				if (transform.getLock().readUnlock(seq2)) {
					return;
				}
			}
		} finally {
			lock.writeUnlock(seq);
		}
	}
	
	/**
	 * Atomically sets this point to the given components
	 * 
	 * @param point
	 */
	public void set(Point p, Quaternion r, Vector3 s) {
		int seq = lock.writeLock();
		try {
			position.directSet(p);
			rotation.directSet(r);
			scale.directSet(s);
		} finally {
			lock.writeUnlock(seq);
		}
	}
	
	public Transform createSum(Transform t){
		Transform r = new Transform();
		r.setPosition(position.add(t.getPosition()));
		r.setRotation(rotation.multiply(t.getRotation()));
		r.setScale(scale.add(t.getScale()));
		return r;
	}

	public Transform getAbsolutePosition(){
		if(parent == null) return this;
		return this.createSum(parent.get().getAbsolutePosition());

	}

	public Transform copy(){
		Transform t = new Transform();
		t.setPosition(new Point(this.position));
		t.setRotation(new Quaternion(this.rotation));
		t.setScale(new Vector3m(this.scale));
		return t;
	}

	public String toString() {
		return getClass().getSimpleName()+ "{" + position + ", "+ rotation + ", " + scale + "}";
	}

	public OptimisticReadWriteLock getLock() {
		return lock;
	}
}
