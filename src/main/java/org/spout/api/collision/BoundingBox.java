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
package org.spout.api.collision;

import org.spout.api.math.Vector3;

public class BoundingBox implements CollisionVolume {
	Vector3 min;
	Vector3 max;

	public BoundingBox(Vector3 min, Vector3 max) {
		this.min = min;
		this.max = max;
	}

	public BoundingBox(Vector3 pos) {
		min = pos;
		max = pos.add(Vector3.ONE);
	}

	public BoundingBox() {
		this(Vector3.ZERO, Vector3.ONE);
	}

	public boolean intersects(BoundingBox b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(BoundingSphere b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(Segment b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(Plane b) {
		return CollisionHelper.checkCollision(this, b);
	}

	public boolean intersects(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return intersects((BoundingBox) other);
		} else if (other instanceof BoundingSphere) {
			return intersects((BoundingSphere) other);
		} else if (other instanceof Segment) {
			return intersects((Segment) other);
		} else if (other instanceof Plane) {
			return intersects((Plane) other);
		}
		return false;
	}

	public boolean contains(CollisionVolume other) {
		if (other instanceof BoundingBox) {
			return containsBoundingBox((BoundingBox) other);
		} else if (other instanceof BoundingSphere) {
			return containsBoundingSphere((BoundingSphere) other);
		} else if (other instanceof Plane) {
			return containsPlane((Plane) other);
		} else if (other instanceof Ray) {
			return containsRay((Ray) other);
		} else if (other instanceof Segment) {
			return containsSegment((Segment) other);
		}
		return false;
	}

	public boolean containsBoundingBox(BoundingBox b) {
		return CollisionHelper.contains(b, this);
	}

	public boolean containsBoundingSphere(BoundingSphere b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsPlane(Plane b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsRay(Ray b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsSegment(Segment b) {
		return CollisionHelper.contains(this, b);
	}

	public boolean containsPoint(Vector3 b) {
		return CollisionHelper.contains(this, b);
	}

	public Vector3 resolve(CollisionVolume start, CollisionVolume end) {
		// TODO Auto-generated method stub
		return null;
	}
}
