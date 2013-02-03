/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.component.Component;
import org.spout.api.component.type.WidgetComponent;
import org.spout.api.event.player.PlayerKeyEvent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.input.InputExecutor;
import org.spout.api.input.InputManager;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;
import org.spout.api.math.IntVector2;

import org.spout.engine.entity.SpoutPlayer;

public class SpoutInputManager implements InputManager {
	private static final Keyboard FOCUS_KEY = Keyboard.KEY_TAB;
	private final Map<Keyboard, String> keyCommands = new HashMap<Keyboard, String>();
	private final Map<Mouse, String> mouseCommands = new HashMap<Mouse, String>();
	
	private final List<InputExecutor> inputExecutors = new ArrayList<InputExecutor>();
	
	private boolean redirected = false;

	public SpoutInputManager()
	{
		bind(Keyboard.get(SpoutInputConfiguration.FORWARD.getString()), "forward");
		bind(Keyboard.get(SpoutInputConfiguration.BACKWARD.getString()), "backward");
		bind(Keyboard.get(SpoutInputConfiguration.LEFT.getString()), "left");
		bind(Keyboard.get(SpoutInputConfiguration.RIGHT.getString()), "right");
		bind(Keyboard.get(SpoutInputConfiguration.UP.getString()), "jump");
		bind(Keyboard.get(SpoutInputConfiguration.DOWN.getString()), "crouch");
		bind(Keyboard.KEY_F2, "dev_console");
		bind(Keyboard.KEY_F3, "debug_infos");
		bind(org.spout.api.input.Mouse.MOUSE_SCROLLDOWN, "select_down");
		bind(org.spout.api.input.Mouse.MOUSE_SCROLLUP, "select_up");
		bind(org.spout.api.input.Mouse.MOUSE_BUTTON0, "left_click");
		bind(org.spout.api.input.Mouse.MOUSE_BUTTON1, "interact");
		bind(org.spout.api.input.Mouse.MOUSE_BUTTON2, "fire_2");
	}
	
	private void onKeyPressed(SpoutPlayer player, Keyboard key, boolean pressed, char ch) {
		String cmd = keyCommands.get(key);
		final PlayerKeyEvent event = Spout.getEventManager().callEvent(new PlayerKeyEvent(player, key, pressed, cmd, ch));
		if (event.isCancelled()) {
			return;
		}

		doCommand(player, cmd, pressed);

		if (Spout.debugMode()) {
			Spout.log("Key " + key + " was " + (pressed ? "pressed" : "released"));
		}

		if (key == FOCUS_KEY && pressed) {
			Screen in = getInputScreen();
			if (in != null) {
				if (org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LSHIFT.getId()) || org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RSHIFT.getId())) {
					in.previousFocus(FocusReason.KEYBOARD_TAB);
				} else {
					in.nextFocus(FocusReason.KEYBOARD_TAB);
				}
			}
		}

		for (WidgetComponent c : getWidgetComponents()) {
			c.onKey(event);
		}
	}

	private void onMouseClicked(SpoutPlayer player, Mouse button, boolean pressed, int x, int y) {
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

	private Screen getInputScreen() {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Client)) {
			throw new IllegalStateException("Cannot access ScreenStack in server mode.");
		}
		return ((Client) engine).getScreenStack().getInputScreen();
	}

	private Set<WidgetComponent> getWidgetComponents() {
		Set<WidgetComponent> components = new HashSet<WidgetComponent>();
		Screen inputScreen = getInputScreen();
		if (inputScreen == null || inputScreen.getFocusedWidget() == null) { //TODO Fix this narrowtux/W1ndwaker as I think this shouldn't be null
			return components;
		}

		for (Component c : inputScreen.getFocusedWidget().values()) {
			if (c instanceof WidgetComponent) {
				components.add((WidgetComponent) c);
			}
		}

		return components;
	}

	public void pollInput(SpoutPlayer player) {
		if (org.lwjgl.input.Keyboard.isCreated()) {
			while (org.lwjgl.input.Keyboard.next()) {
				Keyboard key = Keyboard.get(org.lwjgl.input.Keyboard.getEventKey());
				if (key != null) {
					onKeyPressed(player, key, org.lwjgl.input.Keyboard.getEventKeyState(), org.lwjgl.input.Keyboard.getEventCharacter());
				}
			}
		}

		// Handle mouse
		if (org.lwjgl.input.Mouse.isCreated()) {
			int x = 0, y= 0;
			
			while (org.lwjgl.input.Mouse.next()) {

				// Calculate dx/dy since last event polling
				x = org.lwjgl.input.Mouse.getEventX();
				y = org.lwjgl.input.Mouse.getEventY();
				
				Mouse button = Mouse.get(org.lwjgl.input.Mouse.getEventButton());
				if (button != null) {
					onMouseClicked(player, button, org.lwjgl.input.Mouse.getEventButtonState(), x, y);
					continue;
				}

				// Handle scrolls
				int scroll = org.lwjgl.input.Mouse.getEventDWheel();
				if (scroll < 0) {
					onMouseClicked(player, Mouse.MOUSE_SCROLLUP, true, x, y);
				} else if (scroll > 0) {
					onMouseClicked(player, Mouse.MOUSE_SCROLLDOWN, true, x, y);
				}

				onMouseMove(player, org.lwjgl.input.Mouse.getEventDX(), org.lwjgl.input.Mouse.getEventDY(), org.lwjgl.input.Mouse.getEventX(), org.lwjgl.input.Mouse.getEventY());
			}
		}
	}

	public void execute(float dt){
		for(InputExecutor executor : inputExecutors){
			executor.execute(dt);
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

	public void addInputExecutors(InputExecutor executor) {
		inputExecutors.add(executor);
	}
}
