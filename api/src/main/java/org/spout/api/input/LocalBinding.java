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

import java.util.Objects;
import org.spout.api.ClientOnly;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;

import org.spout.api.entity.state.PlayerInputState.MouseDirection;

/**
 * Represents a binding between an input action and a command.
 */
public class LocalBinding extends InputActionExecutor {

	private final String cmd;

	// TODO allow server to register client bindings; needs client approval
	@ClientOnly
	public LocalBinding(String cmd, Keyboard[] keys, int[] buttons, MouseDirection[] directions) {
		super(keys, buttons, directions);
		this.cmd = cmd;
	}

	@ClientOnly
	public LocalBinding(String cmd, Keyboard... keys) {
		this(cmd, keys, new int[0], new MouseDirection[0]);
	}

	@ClientOnly
	public LocalBinding(String cmd, int... buttons) {
		this(cmd, new Keyboard[0], buttons, new MouseDirection[0]);
	}

	@ClientOnly
	public LocalBinding(String cmd, MouseDirection... directions) {
		this(cmd, new Keyboard[0], new int[0], directions);
	}

	@Override
	public LocalBinding setAsync(boolean async) {
		return (LocalBinding) super.setAsync(async);
	}

	/**
	 * Returns the command that this binding is bound to. When one of the input actions are executed, this command
	 * will be executed.
	 *
	 * @return command bound to input actions
	 */
	public String getCommand() {
		return cmd;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.cmd);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LocalBinding other = (LocalBinding) obj;
		if (!Objects.equals(this.cmd, other.cmd)) {
			return false;
		}
		return true;
	}

	@Override
	public void onKeyboardAction(Player player, Keyboard key, boolean pressed) {
		player.processCommand(cmd, pressed ? "+" : "-");
	}

	@Override
	public void onMouseDirectionAction(Player player, PlayerInputState.MouseDirection direction, float delta) {
		player.processCommand(cmd, "" + delta);
	}
}
