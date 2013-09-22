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

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.spout.api.ClientOnly;
import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;

public abstract class InputActionExecutor {
	private final Keyboard[] keys;
	private final int[] buttons;
	private final PlayerInputState.MouseDirection[] directions;
	private final AtomicBoolean async = new AtomicBoolean(false);

	// TODO allow server to register client bindings; needs client approval
	@ClientOnly
	public InputActionExecutor(Keyboard[] keys, int[] buttons, PlayerInputState.MouseDirection[] directions) {
		if (Spout.getPlatform() != Platform.CLIENT) {
			throw new UnsupportedOperationException("InputActionExecutor can only be created on the client!");
		}
		this.keys = keys;
		this.buttons = buttons;
		this.directions = directions;
	}

	@ClientOnly
	public InputActionExecutor(Keyboard... keys) {
		this(keys, new int[0], new PlayerInputState.MouseDirection[0]);
	}

	@ClientOnly
	public InputActionExecutor(int... buttons) {
		this(new Keyboard[0], buttons, new PlayerInputState.MouseDirection[0]);
	}

	@ClientOnly
	public InputActionExecutor(PlayerInputState.MouseDirection... directions) {
		this(new Keyboard[0], new int[0], directions);
	}

	/**
	 * Returns the key bindings bound to the command specified by {@link #getCommand()}. When any of these are executed, the command will be executed.
	 *
	 * @return key bindings
	 */
	public Keyboard[] getKeyBindings() {
		return keys;
	}

	/**
	 * Returns the mouse bindings bound to the command specified by {@link #getCommand()}. When any of these are executed, the command will be executed.
	 *
	 * @return mouse bindings
	 */
	public int[] getMouseBindings() {
		return buttons;
	}

	/**
	 * Returns the mouse direction bindings bound to the command specified by {@link #getCommand()}. When any of these are executed, the command will be executed.
	 *
	 * @return mouse direction bindings
	 */
	public PlayerInputState.MouseDirection[] getMouseDirectionBindings() {
		return directions;
	}

	/**
	 * Whether this binding can be executed asynchronously
	 *
	 * @return async
	 */
	public boolean isAsync() {
		return async.get();
	}

	/**
	 * Sets whether the binding can be executed asynchronously
	 */
	public InputActionExecutor setAsync(boolean async) {
		this.async.set(async);
		return this;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Arrays.deepHashCode(this.keys);
		hash = 97 * hash + Arrays.hashCode(this.buttons);
		hash = 97 * hash + Arrays.deepHashCode(this.directions);
		hash = 97 * hash + Objects.hashCode(this.async);
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
		final InputActionExecutor other = (InputActionExecutor) obj;
		if (!Arrays.deepEquals(this.keys, other.keys)) {
			return false;
		}
		if (!Arrays.equals(this.buttons, other.buttons)) {
			return false;
		}
		if (!Arrays.deepEquals(this.directions, other.directions)) {
			return false;
		}
		if (!Objects.equals(this.async, other.async)) {
			return false;
		}
		return true;
	}

	public void onKeyboardAction(Player p, Keyboard key, boolean pressed) {		
	}

	public void onMouseDirectionAction(Player p, PlayerInputState.MouseDirection direction, float delta) {
	}
}