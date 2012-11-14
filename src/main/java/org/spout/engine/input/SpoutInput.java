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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.api.chat.ChatArguments;
import org.spout.api.input.InputManager;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;

import org.spout.engine.entity.SpoutPlayer;

public class SpoutInput implements InputManager {
	private final TIntObjectMap<String> keyCommands = new TIntObjectHashMap<String>();
	private final TIntObjectMap<String> mouseCommands = new TIntObjectHashMap<String>();
	private boolean redirected = false;

	private void doKeypress(SpoutPlayer player, int button, boolean pressed) {
		String cmd = keyCommands.get(button);
		doCommand(player,cmd, pressed);

	}

	public void doMousepress(SpoutPlayer player, int button, boolean pressed) {
		String cmd = mouseCommands.get(button);
		doCommand(player, cmd,  pressed);
	}

	public void doMouseDx(SpoutPlayer player,int dx) {
		player.processCommand("+dx", new ChatArguments(dx));
	}

	public void doMouseDy(SpoutPlayer player,int dy) {
		player.processCommand("+dy", new ChatArguments(dy));
	}

	private void doCommand(SpoutPlayer player, String command, boolean pressed) {
		if (command == null) {
			return;
		}
		
		if (pressed) {
			command = "+" + command;
		} else {
			command = "-" + command;
		}

		player.processCommand(command, new ChatArguments());
	}

	@Override
	public void bind(Keyboard key, String command) {
		keyCommands.put(key.getId(), command);
	}

	@Override
	public void bind(Mouse button, String command) {
		mouseCommands.put(button.getId(), command);
	}

	public void pollInput(SpoutPlayer player) {
		/*if (redirected) { // Sorry, but I like to move in my little world
			return;
		}*/

		if(org.lwjgl.input.Keyboard.isCreated()){
			while (org.lwjgl.input.Keyboard.next()) {
				int button = org.lwjgl.input.Keyboard.getEventKey();
				if (button != -1)
					doKeypress(player,button, org.lwjgl.input.Keyboard.getEventKeyState());
			}
		}

		// Handle mouse
		if(org.lwjgl.input.Mouse.isCreated())
			while (org.lwjgl.input.Mouse.next()) {
				// Handle buttons
				int button = org.lwjgl.input.Mouse.getEventButton();
				if (button != -1) {
					doMousepress(player, button, org.lwjgl.input.Mouse.getEventButtonState());
					continue;
				}

				// Handle scrolls
				int scroll = org.lwjgl.input.Mouse.getEventDWheel();
				if (scroll < 0) {
					doMousepress(player, Keyboard.KEY_SCROLLUP.getId(), true);
				} else if (scroll > 0) {
					doMousepress(player, Keyboard.KEY_SCROLLDOWN.getId(), true);
				}

				doMouseDx(player, org.lwjgl.input.Mouse.getDX());
				doMouseDy(player, org.lwjgl.input.Mouse.getDY());
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
