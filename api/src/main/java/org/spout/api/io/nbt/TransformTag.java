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
package org.spout.api.io.nbt;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.math.imaginary.Quaternionf;
import org.spout.math.vector.Vector3f;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;

public class TransformTag extends CompoundTag {
	public TransformTag(String name, Transform t) {
		this(name, t.getPosition(), t.getRotation(), t.getScale());
	}

	public TransformTag(String name, float px, float py, float pz, float qx, float qy, float qz, float qw, float sx, float sy, float sz) {
		this(name, new Vector3f(px, py, pz), new Quaternionf(qx, qy, qz, qw), new Vector3f(sx, sy, sz));
	}

	public TransformTag(String name, Vector3f p, Quaternionf q, Vector3f s) {
		super(name, toMap(p, q, s));
	}

	private static CompoundMap toMap(Vector3f p, Quaternionf q, Vector3f s) {
		CompoundMap map = new CompoundMap();
		map.put(new Vector3Tag("pos", p));
		map.put(new QuaternionTag("rot", q));
		map.put(new Vector3Tag("scale", s));
		return map;
	}

	public static Transform getValue(World w, Tag<?> tag) {
		try {
			return getValue(w, (CompoundTag) tag);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Transform getValue(World w, CompoundTag map) {
		if (map == null || w == null) {
			return null;
		}
		return getValue(w, map.getValue());
	}

	public static Transform getValue(World w, CompoundMap map) {
		if (map == null || w == null) {
			return null;
		}
		Vector3f pVector = Vector3Tag.getValue(map.get("pos"));

		Quaternionf r = QuaternionTag.getValue(map.get("rot"));

		Vector3f s = Vector3Tag.getValue(map.get("scale"));

		if (pVector == null || r == null || s == null) {
			return null;
		}

		Point p = new Point(pVector, w);

		return new Transform(p, r, s);
	}
}
