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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a binding between an input action and a command.
 */
public class Binding {
	private final String cmd;
	private final Keyboard[] keys;
	private final int[] buttons;
	private final AtomicBoolean async = new AtomicBoolean(false);

	public Binding(String cmd, Keyboard[] keys, int[] buttons) {
		this.cmd = cmd;
		this.keys = keys;
		this.buttons = buttons;
	}

	public Binding(String cmd, Keyboard... keys) {
		this(cmd, keys, new int[0]);
	}

	public Binding(String cmd, int... buttons) {
		this(cmd, new Keyboard[0], buttons);
	}

	/**
	 * Returns the command that this binding is bound to. When one of the input
	 * actions are executed, this command will be executed.
	 *
	 * @return command bound to input actions
	 */
	public String getCommand() {
		return cmd;
	}

	/**
	 * Returns the key bindings bound to the command specified by
	 * {@link #getCommand()}. When any of these are executed, the command will
	 * be executed.
	 *
	 * @return key bindings
	 */
	public Keyboard[] getKeyBindings() {
		return keys;
	}

	/**
	 * Returns the mouse bindings bound to the command specified by
	 * {@link #getCommand()}. When any of these are executed, the command will
	 * be executed.
	 *
	 * @return mouse bindings
	 */
	public int[] getMouseBindings() {
		return buttons;
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
	 * 
	 * @param async
	 */
	public Binding setAsync(boolean async) {
		this.async.set(async);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Binding && ((Binding) obj).cmd.equalsIgnoreCase(cmd);
	}
}
