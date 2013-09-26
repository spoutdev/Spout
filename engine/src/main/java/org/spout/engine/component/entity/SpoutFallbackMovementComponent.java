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
package org.spout.engine.component.entity;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.component.Component;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.geo.discrete.Transform;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector3;

public class SpoutFallbackMovementComponent extends Component {

	@Override
	public void onAttached() {
		if (Spout.getPlatform() != Platform.CLIENT) {
			throw new UnsupportedOperationException("SpoutFallbackMovementComponent can only be attached client side!");
		}
	}

	@Override
	public void onTick(float dt) {
		final Client client = (Client) Spout.getEngine();
		final Transform playerTransform = client.getPlayer().getPhysics().getTransform();
		final PlayerInputState state = client.getPlayer().input();
		final float speed = 50f;
		Vector3 motion = Vector3.ZERO;
		if (state.getForward()) {
			motion = motion.add(playerTransform.forwardVector().mul(speed * -dt));
		}
		if (state.getBackward()) {
			motion = motion.add(playerTransform.forwardVector().mul(speed * dt));
		}
		if (state.getLeft()) {
			motion = motion.add(playerTransform.rightVector().mul(speed * -dt)); //TODO getLeftVector
		}
		if (state.getRight()) {
			motion = motion.add(playerTransform.rightVector().mul(speed * dt));
		}
		if (state.getJump()) {
			motion = motion.add(playerTransform.upVector().mul(speed * dt));
		}
		if (state.getCrouch()) {
			motion = motion.add(playerTransform.upVector().mul(speed * -dt));
		}

		client.getPlayer().getPhysics().setRotation(Quaternion.fromAxesAnglesDeg(state.pitch(), state.yaw(), playerTransform.getRotation().getAxesAngleDeg().getZ()));
		
		if (!motion.equals(Vector3.ZERO)) {
			client.getPlayer().getPhysics().translate(motion);
		}

	}
}
