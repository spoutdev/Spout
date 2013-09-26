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
package org.spout.api.input;

import org.spout.api.ClientOnly;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.entity.state.PlayerInputState.MouseDirection;

/**
 * Represents a binding between an input action and a command for (@link PlayerInputState}
 */
public class MovementExecutor extends Binding {
	private final PlayerInputState.Flags flag;

	@ClientOnly
	public MovementExecutor(String cmd, Keyboard[] keys, int[] buttons, MouseDirection[] directions) {
		super(cmd, keys, buttons, directions);
		this.flag = PlayerInputState.Flags.getFlag(cmd);
		if (flag == null && !"PITCH".equalsIgnoreCase(cmd) && !"YAW".equalsIgnoreCase(cmd)) {
			throw new UnsupportedOperationException(cmd + " is not a valid PlayerInputState.Flags");
		}
	}

	@ClientOnly
	public MovementExecutor(String cmd, Keyboard... keys) {
		this(cmd, keys, new int[0], new MouseDirection[0]);
	}

	@ClientOnly
	public MovementExecutor(String cmd, int... buttons) {
		this(cmd, new Keyboard[0], buttons, new MouseDirection[0]);
	}

	@ClientOnly
	public MovementExecutor(String cmd, MouseDirection... directions) {
		this(cmd, new Keyboard[0], new int[0], directions);
	}

	@Override
	public MovementExecutor setAsync(boolean async) {
		return (MovementExecutor) super.setAsync(async);
	}

	@Override
	public void onKeyboardAction(Player player, Keyboard key, boolean pressed) {
		super.onKeyboardAction(player, key, pressed);

		// TODO: this should be fallback only
		player.processInput(pressed ? player.input().withAddedFlag(flag) : player.input().withRemovedFlag(flag));
	}

	@Override
	public void onMouseDirectionAction(Player player, PlayerInputState.MouseDirection d, float delta) {
		super.onMouseDirectionAction(player, d, delta);

		switch (d) {
			case YAW:
				player.processInput(player.input().withAddedYaw(delta));
				break;
			case PITCH:
				player.processInput(player.input().withAddedPitch(delta));
				break;
		}

	}
}
