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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.component.Component;
import org.spout.api.component.components.WidgetComponent;
import org.spout.api.event.player.PlayerKeyboardEvent;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.input.InputManager;
import org.spout.api.input.KeyEvent;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;
import org.spout.api.math.IntVector2;

import org.spout.engine.entity.SpoutPlayer;

public class SpoutInputManager implements InputManager {
	private final Map<Keyboard, String> keyCommands = new HashMap<Keyboard, String>();
	private final Map<Mouse, String> mouseCommands = new HashMap<Mouse, String>();
	private boolean redirected = false;

	private void onKeyPressed(SpoutPlayer player, Keyboard key, boolean pressed) {
		String cmd = keyCommands.get(key);
		if (PlayerKeyboardEvent.getHandlerList().getRegisteredListeners().length > 0) {
			final PlayerKeyboardEvent event = Spout.getEventManager().callEvent(new PlayerKeyboardEvent(player, key, pressed, cmd));
			if (event.isCancelled()) {
				return;
			}
		}

		for (WidgetComponent c : getWidgetComponents()) {
			c.onKey(new KeyEvent(key, pressed));
		}

		doCommand(player, cmd, pressed);
	}

	private void onMousePressed(SpoutPlayer player, Mouse button, boolean pressed, int x, int y) {
		for (WidgetComponent c : getWidgetComponents()) {
			c.onClicked(new IntVector2(x, y), pressed);
		}
		String cmd = mouseCommands.get(button);
		doCommand(player, cmd, pressed);
	}

	private void onMouseMove(SpoutPlayer player, int dx, int dy, int x, int y) {
		// Mouse hasn't moved
		if (dx == 0 && dy == 0) {
			return;
		}

		// Mouse moved on x-axis
		if (!redirected) {
			if (dx != 0) {
				player.processCommand("+dx", new ChatArguments(dx));
			}

			// Mouse moved on y-axis
			if (dy != 0) {
				player.processCommand("+dy", new ChatArguments(dy));
			}
		}

		for (WidgetComponent c : getWidgetComponents()) {
			c.onMouseMove(new IntVector2(x, y));
		}
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

	private Set<WidgetComponent> getWidgetComponents() {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Client)) {
			throw new IllegalStateException("Cannot access ScreenStack in server mode.");
		}

		Set<WidgetComponent> components = new HashSet<WidgetComponent>();
		Screen inputScreen = ((Client) engine).getScreenStack().getInputScreen();
		if (inputScreen == null) {
			return components;
		}

		for (Widget widget : inputScreen.getWidgets()) {
			for (Component c : widget.values()) {
				if (c instanceof WidgetComponent) {
					components.add((WidgetComponent) c);
				}
			}
		}

		return components;
	}

	public void pollInput(SpoutPlayer player) {
		if (org.lwjgl.input.Keyboard.isCreated()) {
			while (org.lwjgl.input.Keyboard.next()) {
				Keyboard key = Keyboard.get(org.lwjgl.input.Keyboard.getEventKey());
				if (key != null) {
					onKeyPressed(player, key, org.lwjgl.input.Keyboard.getEventKeyState());
				}
			}
		}

		// Handle mouse
		if (org.lwjgl.input.Mouse.isCreated()) {
			while (org.lwjgl.input.Mouse.next()) {

				// Handle buttons
				int x = org.lwjgl.input.Mouse.getX(), y = org.lwjgl.input.Mouse.getY();
				Mouse button = Mouse.get(org.lwjgl.input.Mouse.getEventButton());
				if (button != null) {
					onMousePressed(player, button, org.lwjgl.input.Mouse.getEventButtonState(), x, y);
					continue;
				}

				// Handle scrolls
				int scroll = org.lwjgl.input.Mouse.getEventDWheel();
				if (scroll < 0) {
					onMousePressed(player, Mouse.MOUSE_SCROLLUP, true, x, y);
				} else if (scroll > 0) {
					onMousePressed(player, Mouse.MOUSE_SCROLLDOWN, true, x, y);
				}

				onMouseMove(player, org.lwjgl.input.Mouse.getDX(), org.lwjgl.input.Mouse.getDY(), org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY());
			}
		}
	}

	@Override
	public void bind(Keyboard key, String command) {
		keyCommands.put(key, command);
	}

	@Override
	public void bind(Mouse button, String command) {
		mouseCommands.put(button, command);
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
