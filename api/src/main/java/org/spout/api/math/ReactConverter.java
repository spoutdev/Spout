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
package org.spout.api.math;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;

public class ReactConverter {
	//Spout -> React
	public static org.spout.physics.math.Vector3 toReactVector3(final Vector3 spoutVector3) {
		return new org.spout.physics.math.Vector3(spoutVector3.x, spoutVector3.y, spoutVector3.z);
	}

	public static org.spout.physics.math.Vector3 toReactVector3(final float x, final float y, final float z) {
		return new org.spout.physics.math.Vector3(x, y, z);
	}

	public static org.spout.physics.math.Quaternion toReactQuaternion(final Quaternion spoutQuaternion) {
		return new org.spout.physics.math.Quaternion(spoutQuaternion.x, spoutQuaternion.y, spoutQuaternion.z, spoutQuaternion.w);
	}

	public static org.spout.physics.math.Quaternion toReactQuaternion(final float w, final float x, final float y, final float z) {
		return new org.spout.physics.math.Quaternion(x, y, z, w);
	}

	public static org.spout.physics.math.Transform toReactTransform(final Transform spoutTransform) {
		return new org.spout.physics.math.Transform(toReactVector3(spoutTransform.getPosition()), toReactQuaternion(spoutTransform.getRotation()));
	}

	//React -> Spout
	public static Vector3 toSpoutVector3(final org.spout.physics.math.Vector3 reactVector3) {
		return new Vector3(reactVector3.getX(), reactVector3.getY(), reactVector3.getZ());
	}

	public static Quaternion toSpoutQuaternion(final org.spout.physics.math.Quaternion reactQuaternion) {
		return new Quaternion(reactQuaternion.getX(), reactQuaternion.getY(), reactQuaternion.getZ(), reactQuaternion.getW(), true);
	}

	public static Transform toSpoutTransform(final org.spout.physics.math.Transform reactTransform, final World world, final Vector3 scale) {
		return new Transform(new Point(toSpoutVector3(reactTransform.getPosition()), world), new Quaternion(toSpoutQuaternion(reactTransform.getOrientation())), scale);
	}
}