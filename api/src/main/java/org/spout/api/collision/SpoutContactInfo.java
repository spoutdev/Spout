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
package org.spout.api.collision;

import org.spout.api.math.ReactConverter;
import org.spout.api.math.Vector3;
import org.spout.physics.collision.ContactInfo;

public class SpoutContactInfo {
	private final ContactInfo wrapped;

	public SpoutContactInfo(final ContactInfo wrapped) {
		this.wrapped = wrapped;
	}

	public Vector3 getFirstContactPoint() {
		return ReactConverter.toSpoutVector3(wrapped.getFirstLocalPoint());
	}

	public void setFirstContactPoint(final Vector3 vec) {
		wrapped.setFirstLocalPoint(ReactConverter.toReactVector3(vec));
	}

	public Vector3 getSecondContactPoint() {
		return ReactConverter.toSpoutVector3(wrapped.getSecondLocalPoint());
	}

	public void setSecondContactPoint(final Vector3 vec) {
		wrapped.setSecondLocalPoint(ReactConverter.toReactVector3(vec));
	}

	public Vector3 getNormal() {
		return ReactConverter.toSpoutVector3(wrapped.getNormal());
	}

	public void setNormal(final Vector3 vec) {
		wrapped.setNormal(ReactConverter.toReactVector3(vec));
	}

	public float getPenetrationDepth() {
		return wrapped.getPenetrationDepth();
	}

	public void setPenetrationDepth(final float pDepth) {
		wrapped.setPenetrationDepth(pDepth);
	}
}