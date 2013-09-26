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
package org.spout.engine.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.Display;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.entity.state.PlayerInputState.MouseDirection;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.input.Binding;
import org.spout.api.input.InputActionExecutor;
import org.spout.api.input.InputManager;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;
import org.spout.api.input.MovementExecutor;
import org.spout.api.math.IntVector2;

import org.spout.engine.protocol.builtin.message.ClickRequestMessage;

public class SpoutInputManager implements InputManager {
	private static final Keyboard FOCUS_KEY = Keyboard.KEY_TAB;
	private final Set<InputActionExecutor> actions = new HashSet<>();
	private boolean redirected = false;

	public SpoutInputManager() {
		// TODO: these should be fallback ONLY.
		bind(new MovementExecutor(PlayerInputState.Flags.FORWARD.name(), Keyboard.valueOf(SpoutInputConfiguration.FORWARD.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.BACKWARD.name(), Keyboard.valueOf(SpoutInputConfiguration.BACKWARD.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.LEFT.name(), Keyboard.valueOf(SpoutInputConfiguration.LEFT.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.RIGHT.name(), Keyboard.valueOf(SpoutInputConfiguration.RIGHT.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.JUMP.name(), Keyboard.valueOf(SpoutInputConfiguration.UP.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.CROUCH.name(), Keyboard.valueOf(SpoutInputConfiguration.DOWN.getString().toUpperCase())).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.SELECT_DOWN.name(), Mouse.SCROLL_DOWN).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.SELECT_UP.name(), Mouse.SCROLL_UP).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.LEFT_CLICK.name(), Mouse.BUTTON_LEFT).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.INTERACT.name(), Mouse.BUTTON_RIGHT).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.Flags.FIRE_2.name(), Mouse.BUTTON_MIDDLE).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.MouseDirection.PITCH.name(), MouseDirection.PITCH).setAsync(true));
		bind(new MovementExecutor(PlayerInputState.MouseDirection.YAW.name(), MouseDirection.YAW).setAsync(true));
	}

	@Override
	public void bind(InputActionExecutor binding) {
		actions.add(binding);
	}

	@Override
	public void unbind(InputActionExecutor binding) {
		actions.remove(binding);
	}

	@Override
	public Set<InputActionExecutor> getInputActionExecutors() {
		return Collections.unmodifiableSet(actions);
	}

	@Override
	public Set<InputActionExecutor> getKeyInputActionExecutorsFor(Keyboard key) {
		Set<InputActionExecutor> bound = new HashSet<>();
		for (InputActionExecutor binding : actions) {
			for (Keyboard k : binding.getKeyBindings()) {
				if (k == key) {
					bound.add(binding);
					break;
				}
			}
		}
		return bound;
	}

	@Override
	public Set<InputActionExecutor> getMouseInputActionExecutorsFor(int mouse) {
		Set<InputActionExecutor> bound = new HashSet<>();
		for (InputActionExecutor binding : actions) {
			for (int button : binding.getMouseBindings()) {
				if (button == mouse) {
					bound.add(binding);
					break;
				}
			}
		}
		return bound;
	}

	@Override
	public Set<InputActionExecutor> getMouseDirectionInputActionExecutorsFor(MouseDirection direction) {
		Set<InputActionExecutor> bound = new HashSet<>();
		for (InputActionExecutor binding : actions) {
			for (MouseDirection d : binding.getMouseDirectionBindings()) {
				if (d == direction) {
					bound.add(binding);
					break;
				}
			}
		}
		return bound;
	}

	public void pollInput(Player player) {
		if (org.lwjgl.input.Keyboard.isCreated()) {
			while (org.lwjgl.input.Keyboard.next()) {
				Keyboard key = Keyboard.get(org.lwjgl.input.Keyboard.getEventKey());
				if (key != null) {
					onKeyPressed(player, key, org.lwjgl.input.Keyboard.getEventKeyState(), org.lwjgl.input.Keyboard.getEventCharacter());
				}
			}
		}

		// Handle mouse
		if (org.lwjgl.input.Mouse.isCreated() && Display.isActive()) {
			// TODO can these be bytes, or should they be ints
			int x, y;
			while (org.lwjgl.input.Mouse.next()) {
				// Calculate dx/dy since last event polling
				x = org.lwjgl.input.Mouse.getEventX();
				y = org.lwjgl.input.Mouse.getEventY();

				int info;
				if ((info = org.lwjgl.input.Mouse.getEventButton()) != -1) { // -1 if no button clicked
					onMouseClicked(player, info, org.lwjgl.input.Mouse.getEventButtonState(), x, y);
				} else if ((info = org.lwjgl.input.Mouse.getEventDWheel()) != 0) {
					// Handle scrolls
					if (info < 0) {
						onMouseClicked(player, Mouse.SCROLL_UP, true, x, y);
					} else if (info > 0) {
						onMouseClicked(player, Mouse.SCROLL_DOWN, true, x, y);
					}
				} else {
					onMouseMove(player, org.lwjgl.input.Mouse.getEventDX(), org.lwjgl.input.Mouse.getEventDY(), org.lwjgl.input.Mouse.getEventX(), org.lwjgl.input.Mouse.getEventY());
				}
			}
		}
	}

	private void onKeyPressed(Player player, Keyboard key, boolean pressed, char ch) {
		final PlayerKeyEvent event = Spout.getEventManager().callEvent(new PlayerKeyEvent(player, key, pressed, ch));
		if (event.isCancelled()) {
			return;
		}

		if (Spout.debugMode()) {
			Spout.log("Key " + key + " was " + (pressed ? "pressed" : "released"));
		}

		// SHIFT + TAB changes the focus index
		Screen in = getInputScreen();
		if (key == FOCUS_KEY && pressed) {
			if (in != null) {
				if (org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LSHIFT.getId()) || org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RSHIFT.getId())) {
					in.previousFocus(FocusReason.KEYBOARD_TAB);
				} else {
					in.nextFocus(FocusReason.KEYBOARD_TAB);
				}
			}
		}

		// send event to the focused widget
		if (in != null) {
			Widget w = in.getFocusedWidget();
			if (w != null) {
				w.onKey(event);
			}
		}

		executeBindings(getKeyInputActionExecutorsFor(key), player, key, pressed);
	}

	private void onMouseClicked(Player player, int button, boolean pressed, int x, int y) {
		//TODO Just testing - also, check int -> byte
		player.getNetwork().getSession().send(new ClickRequestMessage((byte) x, (byte) y, ClickRequestMessage.Action.LEFT));

		PlayerClickEvent event = Spout.getEventManager().callEvent(new PlayerClickEvent(player, button, pressed, new IntVector2(x, y)));
		if (event.isCancelled()) {
			return;
		}

		if (Spout.debugMode()) {
			Spout.log("Mouse clicked at {" + x + "," + y + "} was " + (pressed ? "pressed" : "released"));
		}

		Screen s = getInputScreen();
		if (s != null) {
			Widget w = s.getWidgetAt(x, y);
			if (w != null) {
				Widget fw = s.getFocusedWidget();
				if (fw != null && !fw.equals(w)) {
					s.setFocus(w, FocusReason.CLICKED);
				}
				w.onClick(event);
			}
		}

		executeBindings(getMouseInputActionExecutorsFor(button), player, Keyboard.get(button), pressed);
	}

	private void onMouseMove(Player player, int dx, int dy, int x, int y) {
		// Mouse hasn't moved
		if (dx == 0 && dy == 0) {
			return;
		}

		Screen screen = getInputScreen();
		if (screen != null) {
			IntVector2 prev = new IntVector2(x - dx, y - dy);
			IntVector2 pos = new IntVector2(x, y);
			for (Widget w : screen.getWidgets()) {
				w.onMouseMoved(prev, pos, w == screen.getWidgetAt(x, y));
			}
		}

		// Mouse moved on x-axis
		if (!redirected) {
			boolean invert = SpoutInputConfiguration.INVERT_MOUSE.getBoolean();
			if (dx != 0) {
				executeDirectionBindings(getMouseDirectionInputActionExecutorsFor(MouseDirection.YAW), player, MouseDirection.YAW, (PlayerInputState.MOUSE_SENSITIVITY * dx * (invert ? -1 : 1)));
			}

			// Mouse moved on y-axis
			if (dy != 0) {
				executeDirectionBindings(getMouseDirectionInputActionExecutorsFor(MouseDirection.PITCH), player, MouseDirection.PITCH, (PlayerInputState.MOUSE_SENSITIVITY * dy * (invert ? 1 : -1))); 
			}
		}
	}

	private void executeBindings(Set<InputActionExecutor> bindings, Player player, Keyboard key, boolean pressed) {
		String arg = pressed ? "+" : "-";
		// Do bindings
		for (InputActionExecutor binding : bindings) {
			KeyboardActionTask task = new KeyboardActionTask(player, binding, key, pressed);
			if (binding.isAsync()) {
				// Execute async
				task.run();
			} else {
				// Queue sync
				player.getEngine().getScheduler().scheduleSyncDelayedTask(Spout.getPluginManager().getMetaPlugin(), task);
			}
		}
	}

	private void executeDirectionBindings(Set<InputActionExecutor> bindings, Player player, PlayerInputState.MouseDirection direction, float arg) {
		// Do bindings
		for (InputActionExecutor binding : bindings) {
			MouseActionTask task = new MouseActionTask(player, binding, direction, arg);
			if (binding.isAsync()) {
				// Execute async
				task.run();
			} else {
				// Queue sync
				player.getEngine().getScheduler().scheduleSyncDelayedTask(Spout.getPluginManager().getMetaPlugin(), task);
			}
		}
	}

	private static class KeyboardActionTask implements Runnable {
		private final Player player;
		private final InputActionExecutor e;
		private final Keyboard key;
		private final boolean pressed;

		KeyboardActionTask(Player player, InputActionExecutor e, Keyboard key, boolean pressed) {
			this.player = player;
			this.e = e;
			this.key = key;
			this.pressed = pressed;
		}

		@Override
		public void run() {
			e.onKeyboardAction(player, key, pressed);
		}
	}

	private static class MouseActionTask implements Runnable {
		private final Player player;
		private final InputActionExecutor e;
		private final PlayerInputState.MouseDirection direction;
		private final float arg;

		MouseActionTask(Player player, InputActionExecutor e, PlayerInputState.MouseDirection direction, float arg) {
			this.player = player;
			this.e = e;
			this.direction = direction;
			this.arg = arg;
		}

		@Override
		public void run() {
			e.onMouseDirectionAction(player, direction, arg);
		}
	}

	private Screen getInputScreen() {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Client)) {
			throw new IllegalStateException("Cannot access ScreenStack in server mode.");
		}
		return ((Client) engine).getScreenStack().getInputScreen();
	}

	@Override
	public boolean isRedirected() {
		return redirected;
	}

	@Override
	public void setRedirected(boolean redirect) {
		redirected = redirect;
	}

	@Override
	public boolean isKeyDown(Keyboard key) {
		return org.lwjgl.input.Keyboard.isKeyDown(key.getId());
	}

	@Override
	public boolean isButtonDown(int button) {
		return org.lwjgl.input.Mouse.isButtonDown(button);
	}
}
