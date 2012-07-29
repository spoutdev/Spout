/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.keyboard.Input;
import org.spout.engine.SpoutEngine;

import gnu.trove.map.hash.TIntObjectHashMap;

public class SpoutInput implements Input {
	private static final int KEY_SCROLLUP = 0xdada;
	private static final int KEY_SCROLLDOWN = 0xfee1bad;

	TIntObjectHashMap<String> keyCommands = new TIntObjectHashMap<String>();
	TIntObjectHashMap<String> mouseCommands = new TIntObjectHashMap<String>();
	private boolean redirected = false;

	public SpoutInput() {
		bind("KEY_W", "+Forward");
	}

	public void doKeypress(int key, boolean pressed) {
		String cmd = keyCommands.get(key);
		if (cmd == null)
			return;

		if (cmd.startsWith("+") && !pressed) {
			cmd = cmd.replaceFirst("\\+", "-");
		}
		((SpoutEngine) Spout.getEngine()).getCommandSource().sendCommand(cmd, new ChatArguments());
	}

	public void doMousepress(int button, boolean pressed) {
		String cmd = mouseCommands.get(button);
		if (cmd == null)
			return;

		if (cmd.startsWith("+") && !pressed) {
			cmd = cmd.replaceFirst("\\+", "-");
		}
		((SpoutEngine) Spout.getEngine()).getCommandSource().sendCommand(cmd, new ChatArguments());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spout.engine.input.Input#bind(java.lang.String,
	 * java.lang.String)
	 */
	public void bind(String key, String command) {
		if (key.startsWith("KEY")) {
			int k = getKey(key);
			keyCommands.put(k, command);
		}

		if (key.startsWith("MOUSE")) {
			int k = Mouse.getButtonIndex(key);
			mouseCommands.put(k, command);
		}

		if (key.startsWith("AXIS")) {

		}
	}

	private int getKey(String name) {
		if (name.contains("SCROLLUP")) {
			return KEY_SCROLLUP;
		}

		if (name.contains("SCROLLDOWN")) {
			return KEY_SCROLLDOWN;
		}

		return Keyboard.getKeyIndex(name);
	}

	public void pollInput() {
		if (redirected) {
			return;
		}

		// Handle keyboard
		while (Keyboard.next()) {
			doKeypress(Keyboard.getEventKey(), Keyboard.getEventKeyState());
		}

		// Handle mouse
		while (Mouse.next()) {
			// Handle buttons
			int button = Mouse.getEventButton();
			if (button != -1) {
				doMousepress(button, Mouse.getEventButtonState());
				continue;
			}

			// Handle scrolls
			int scroll = Mouse.getEventDWheel();
			if (scroll < 0) {
				doKeypress(KEY_SCROLLUP, true);
			} else if (scroll > 0) {
				doKeypress(KEY_SCROLLDOWN, true);
			}

			// Handle movement
			// TODO
		}
	}

	@Override
	public boolean isRedirected() {
		return redirected;
	}

	@Override
	public void setRedirected(boolean redirect) {
		redirected = redirect;
	}
}
