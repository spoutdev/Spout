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
package org.spout.engine.component.entity;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedKey;
import org.spout.api.math.QuaternionMath;
import org.spout.api.math.Vector3;

public class MovementValidator extends EntityComponent {
	public static final String RECEIVED_TRANSFORM = "RECEIVED_TRANSFORM";

	private Player player;
	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new UnsupportedOperationException("FallbackInputManager must be attached to a player");
		}
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("FallbackInputHandler must be attached on the server");
		}
		player = (Player) getOwner();
	}

	@Override
	public void onTick(float dt) {
		final float speed = 5f;
		PlayerInputState inputState = player.input();
		Transform playerTransform = player.getScene().getTransform();
		final Vector3 motion;
		if (inputState.getForward()) {
			motion = playerTransform.forwardVector().multiply(speed * -dt);
		} else if (inputState.getBackward()) {
			motion = playerTransform.forwardVector().multiply(speed * dt);
		} else if (inputState.getLeft()) {
			motion = playerTransform.rightVector().multiply(speed * -dt); //TODO getLeftVector
		} else if (inputState.getRight()) {
			motion = playerTransform.rightVector().multiply(speed * dt);
		} else if (inputState.getJump()) {
			motion = playerTransform.upVector().multiply(speed * dt);
		} else if (inputState.getCrouch()) {
			motion = playerTransform.upVector().multiply(speed * -dt);
		} else {
			playerTransform.setRotation(QuaternionMath.rotation(5 * inputState.pitch(), 5 * inputState.yaw(), playerTransform.getRotation().getRoll()));
			player.getScene().setTransform(playerTransform);
			return;
		}
		
		Transform old = player.getSession().getDataMap().get(RECEIVED_TRANSFORM, (Transform) null);
		if (old == null ||
			Math.abs(old.getPosition().getX() - playerTransform.getPosition().getX()) > .2f ||
			Math.abs(old.getPosition().getY() - playerTransform.getPosition().getY()) > .2f ||
			Math.abs(old.getPosition().getZ() - playerTransform.getPosition().getZ()) > .2f
			) {
			playerTransform.translateAndSetRotation(motion, QuaternionMath.rotation(5 * inputState.pitch(), 5 * inputState.yaw(), playerTransform.getRotation().getRoll()));
			player.getScene().setTransform(playerTransform);
		}
		
	}
	

}
