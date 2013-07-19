/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
	 * Binds an input action to a command.
	 *
	 * @param binding to bind
	 */
	public void bind(Binding binding);

	/**
	 * Unbinds an input binding.
	 *
	 * @param cmd to unbind
	 */
	public void unbind(String cmd);

	/**
	 * Unbinds an input binding.
	 *
	 * @param binding to unbind.
	 */
	public void unbind(Binding binding);

	/**
	 * Returns a set of all the {@link Binding}s.
	 *
	 * @return all bound bindings
	 */
	public Set<Binding> getBindings();

	/**
	 * Returns the bindings that are bound to the specified key.
	 *
	 * @param key that key is bound to
	 * @return all bindings bound to specified key
	 */
	public Set<Binding> getKeyBindingsFor(Keyboard key);

	/**
	 * Returns the bindings that are bound to the specified mouse button.
	 *
	 * @param button button bound
	 * @return bindings bound to specified button
	 */
	public Set<Binding> getMouseBindingsFor(int button);

	/**
	 * Returns the bindings that are bound to the specified mouse direction.
	 *
	 * @param direction mouse direction bound
	 * @return bindings bound to specified MouseDirection
	 */
	public Set<Binding> getMouseDirectionBindingsFor(MouseDirection direction);

	/**
	 * Returns a set of {@link InputExecutor} that are added.
	 *
	 * @return input executors
	 */
	public Set<InputExecutor> getInputExecutors();

	/**
	 * Register a input executor called each frame.
	 *
	 * @param executor
	 */
	public void addInputExecutor(InputExecutor executor);

	/**
	 * Removes an input executor called each frame.
	 *
	 * @param executor
	 */
	public void removeInputExecutor(InputExecutor executor);

	/**
	 * Returns true if the input is redirected from this.
	 *
	 * @return
	 */
	public boolean isRedirected();

	/**
	 * Sets if the input should be redirected.
	 *
	 * @param redirect
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
