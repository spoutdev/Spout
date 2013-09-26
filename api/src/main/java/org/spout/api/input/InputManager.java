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

import java.util.Set;

import org.spout.api.entity.state.PlayerInputState.MouseDirection;

/**
 * Represents the input of Spout.
 */
public interface InputManager {
	/**
	 * Binds an input action to an InputActionExecutor
	 *
	 * @param binding to bind
	 */
	public void bind(InputActionExecutor binding);

	/**
	 * Unbinds an input binding.
	 *
	 * @param binding to unbind.
	 */
	public void unbind(InputActionExecutor binding);

	/**
	 * Returns a set of all the {@link InputActionExecutor}s.
	 *
	 * @return all bound bindings
	 */
	public Set<InputActionExecutor> getInputActionExecutors();

	/**
	 * Returns the bindings that are bound to the specified key.
	 *
	 * @param key that key is bound to
	 * @return all bindings bound to specified key
	 */
	public Set<InputActionExecutor> getKeyInputActionExecutorsFor(Keyboard key);

	/**
	 * Returns the bindings that are bound to the specified mouse button.
	 *
	 * @param button button bound
	 * @return bindings bound to specified button
	 */
	public Set<InputActionExecutor> getMouseInputActionExecutorsFor(int button);

	/**
	 * Returns the bindings that are bound to the specified mouse direction.
	 *
	 * @param direction mouse direction bound
	 * @return bindings bound to specified MouseDirection
	 */
	public Set<InputActionExecutor> getMouseDirectionInputActionExecutorsFor(MouseDirection direction);


	/**
	 * Returns true if the input is redirected from this.
	 */
	public boolean isRedirected();

	/**
	 * Sets if the input should be redirected.
	 */
	public void setRedirected(boolean redirect);

	/**
	 * Returns true if the specified {@link Keyboard} is currently pressed.
	 *
	 * @param key to check
	 * @return true if pressed
	 */
	public boolean isKeyDown(Keyboard key);

	/**
	 * Returns true if the specified mouse button is pressed.
	 *
	 * @param button to check
	 * @return true if pressed
	 */
	public boolean isButtonDown(int button);
}
