/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.filesystem.fields;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.nbt.holder.FieldHolder;
import org.spout.nbt.holder.FieldHolderField;
import org.spout.nbt.holder.FieldValue;

/**
 * Field to represent a Transform
 */
public class TransformField extends FieldHolderField<TransformField.Holder> {
	public static final TransformField INSTANCE = new TransformField();
	public TransformField() {
		super(Holder.class);
	}

	public static class Holder extends FieldHolder {
		private final FieldValue<Vector3> pos = FieldValue.from("pos", Vector3Field.INSTANCE, Vector3.ZERO),
				scale = FieldValue.from("scale", Vector3Field.INSTANCE, Vector3.ONE);
		private final FieldValue<Quaternion> rot = FieldValue.from("rot", QuaternionField.INSTANCE, Quaternion.IDENTITY);

		public Holder() {
			addFields(pos, rot, scale);
		}

		public Holder(Transform t) {
			this();
			pos.set(t.getPosition());
			rot.set(t.getRotation());
			scale.set(t.getScale());
		}

		public Transform toTransform(World world) {
			Point p = new Point(pos.get(), world);
			return new Transform(p, rot.get(), scale.get());
		}
	}
}
