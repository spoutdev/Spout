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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.spout.api.chat.ChatArguments;
import org.spout.api.keyboard.Input;
import org.spout.api.keyboard.Keyboard;
import org.spout.engine.entity.SpoutPlayer;

public class SpoutInput implements Input {
	private static final int KEY_SCROLLUP = 0xdada;
	private static final int KEY_SCROLLDOWN = 0xfee1bad;

	Map<Integer, String> keyCommands = new HashMap<Integer, String>();
	TIntObjectHashMap<String> mouseCommands = new TIntObjectHashMap<String>();
	private boolean redirected = false;

	public SpoutInput() {
		bind2(org.lwjgl.input.Keyboard.KEY_W, "+Forward");
		bind2(org.lwjgl.input.Keyboard.KEY_S, "+BackWard");
		bind2(org.lwjgl.input.Keyboard.KEY_A, "+Left");
		bind2(org.lwjgl.input.Keyboard.KEY_D, "+Right");
		bind2(org.lwjgl.input.Keyboard.KEY_SPACE, "+Jump");
		bind2(org.lwjgl.input.Keyboard.KEY_LSHIFT, "+Crouch");
		bind("KEY_SCROLLDOWN", "+Select_Down");
		bind("KEY_SCROLLUP", "+Select_Up");
		bind("MOUSE_BUTTON0", "+FIRE_1");
		bind("MOUSE_BUTTON1", "+INTERACT");
		bind("MOUSE_BUTTON2", "+FIRE_2");
	}

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

	private void doCommand(SpoutPlayer player,String command, boolean pressed) {
		if (command == null)
			return;

		if (command.startsWith("+") && !pressed) {
			command = command.replaceFirst("\\+", "-");
		}
		player.processCommand(command, new ChatArguments());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.spout.engine.input.Input#bind(org.spout.keyboard.Keyboard,
	 * java.lang.String)
	 */
	@Override
	public void bind(Keyboard key, String command) {
		//keyCommands.put(key, command);
	}

	public void bind2(int key, String command) {
		keyCommands.put(key, command);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.spout.engine.input.Input#bind(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void bind(String key, String command) {
		key = key.toUpperCase();
		if (key.startsWith("KEY_")) {
			String name = key.substring(4);
			if (name.equals("SCROLLDOWN")) {
				mouseCommands.put(KEY_SCROLLDOWN, command);
			} else if (name.equals("SCROLLUP")) {
				mouseCommands.put(KEY_SCROLLUP, command);
			} else {
				bind(Keyboard.valueOf(key), command);
			}
		} else if (key.startsWith("MOUSE")) {
			int k = Mouse.getButtonIndex(key);
			mouseCommands.put(k, command);
		} else if (key.startsWith("AXIS")) {
		}
	}

	public void pollInput(SpoutPlayer player) {
		if (redirected) {
			return;
		}

		if(org.lwjgl.input.Keyboard.isCreated()){
			while (org.lwjgl.input.Keyboard.next()) {
				int button = org.lwjgl.input.Keyboard.getEventKey();
				if (button != -1)
					doKeypress(player,button, org.lwjgl.input.Keyboard.getEventKeyState());
			}
		}

		// Handle mouse
		if(Mouse.isCreated())
			while (Mouse.next()) {
				// Handle buttons
				int button = Mouse.getEventButton();
				if (button != -1) {
					doMousepress(player, button, Mouse.getEventButtonState());
					continue;
				}

				// Handle scrolls
				int scroll = Mouse.getEventDWheel();
				if (scroll < 0) {
					doMousepress(player, KEY_SCROLLUP, true);
				} else if (scroll > 0) {
					doMousepress(player, KEY_SCROLLDOWN, true);
				}

				doMouseDx(player, Mouse.getDX());
				doMouseDy(player, Mouse.getDY());
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
