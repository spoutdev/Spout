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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.*;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.input.Binding;
import org.spout.api.input.InputExecutor;
import org.spout.api.input.InputManager;
import org.spout.api.input.Keyboard;
import org.spout.api.input.Mouse;
import org.spout.api.math.IntVector2;
import org.spout.api.math.QuaternionMath;
import org.spout.api.math.Vector3;

import org.spout.engine.component.entity.SpoutSceneComponent;
import org.spout.engine.protocol.builtin.message.ClickRequestMessage;

public class SpoutInputManager implements InputManager {
	private static final Keyboard FOCUS_KEY = Keyboard.KEY_TAB;
	private final Set<Binding> bindings = new HashSet<Binding>();
	private final Set<InputExecutor> inputExecutors = new HashSet<InputExecutor>();
	
	private boolean redirected = false;

	public SpoutInputManager() {
		bind(new Binding("forward", Keyboard.valueOf(SpoutInputConfiguration.FORWARD.getString().toUpperCase())).setAsync(true));
		bind(new Binding("backward", Keyboard.valueOf(SpoutInputConfiguration.BACKWARD.getString().toUpperCase())).setAsync(true));
		bind(new Binding("left", Keyboard.valueOf(SpoutInputConfiguration.LEFT.getString().toUpperCase())).setAsync(true));
		bind(new Binding("right", Keyboard.valueOf(SpoutInputConfiguration.RIGHT.getString().toUpperCase())).setAsync(true));
		bind(new Binding("jump", Keyboard.valueOf(SpoutInputConfiguration.UP.getString().toUpperCase())).setAsync(true));
		bind(new Binding("crouch", Keyboard.valueOf(SpoutInputConfiguration.DOWN.getString().toUpperCase())).setAsync(true));
		bind(new Binding("select_down", Mouse.SCROLL_DOWN).setAsync(true));
		bind(new Binding("select_up", Mouse.SCROLL_UP).setAsync(true));
		bind(new Binding("left_click", Mouse.BUTTON_LEFT).setAsync(true));
		bind(new Binding("interact", Mouse.BUTTON_RIGHT).setAsync(true));
		bind(new Binding("fire_2", Mouse.BUTTON_MIDDLE).setAsync(true));
	}

	@Override
	public void bind(Binding binding) {
		bindings.add(binding);
	}

	@Override
	public void unbind(String cmd) {
		for (Binding binding : bindings) {
			if (binding.getCommand().equalsIgnoreCase(cmd)) {
				bindings.remove(binding);
				return;
			}
		}
	}

	@Override
	public void unbind(Binding binding) {
		bindings.remove(binding);
	}

	@Override
	public Set<Binding> getBindings() {
		return Collections.unmodifiableSet(bindings);
	}

	@Override
	public Set<Binding> getKeyBindingsFor(Keyboard key) {
		Set<Binding> bound = new HashSet<Binding>();
		for (Binding binding : bindings) {
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
	public Set<Binding> getMouseBindingsFor(int mouse) {
		Set<Binding> bound = new HashSet<Binding>();
		for (Binding binding : bindings) {
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
	public Set<InputExecutor> getInputExecutors() {
		return Collections.unmodifiableSet(inputExecutors);
	}

	@Override
	public void addInputExecutor(InputExecutor executor) {
		inputExecutors.add(executor);
	}

	@Override
	public void removeInputExecutor(InputExecutor executor) {
		inputExecutors.remove(executor);
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
			int x, y;
			while (org.lwjgl.input.Mouse.next()) {

				// Calculate dx/dy since last event polling
				x = org.lwjgl.input.Mouse.getEventX();
				y = org.lwjgl.input.Mouse.getEventY();

				int button = org.lwjgl.input.Mouse.getEventButton();
				if (button != -1) { // -1 if no button clicked
					onMouseClicked(player, button, org.lwjgl.input.Mouse.getEventButtonState(), x, y);
					continue;
				}

				// Handle scrolls
				int scroll = org.lwjgl.input.Mouse.getEventDWheel();
				if (scroll < 0) {
					onMouseClicked(player, Mouse.SCROLL_UP, true, x, y);
				} else if (scroll > 0) {
					onMouseClicked(player, Mouse.SCROLL_DOWN, true, x, y);
				}

				onMouseMove(player, org.lwjgl.input.Mouse.getEventDX(), org.lwjgl.input.Mouse.getEventDY(), org.lwjgl.input.Mouse.getEventX(), org.lwjgl.input.Mouse.getEventY());
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

		executeBindings(getKeyBindingsFor(key), player, pressed);
	}

	private void onMouseClicked(Player player, int button, boolean pressed, int x, int y) {
		//TODO Just testing
		player.getSession().send(new ClickRequestMessage(x, y, ClickRequestMessage.Action.LEFT));

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

		executeBindings(getMouseBindingsFor(button), player, pressed);
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
				player.processInput(player.input().withAddedYaw(PlayerInputState.MOUSE_SENSITIVITY * dx * (invert ? 1 : -1)));
			}

			// Mouse moved on y-axis
			if (dy != 0) {
				player.processInput(player.input().withAddedPitch(PlayerInputState.MOUSE_SENSITIVITY * dy * (invert ? -1 : 1)));
			}
		}
	}

	private void executeBindings(Set<Binding> bindings, Player player, boolean pressed) {
		String arg = pressed ? "+" : "-";
		//Queue up sync bindings first
		for (Binding binding : bindings) {
			if (!binding.isAsync()) {
				player.getEngine().getScheduler().scheduleSyncDelayedTask(null, new BindingTask(player, binding.getCommand(), arg));
			}
		}
		//Execute async bindings
		for (Binding binding : bindings) {
			if (binding.isAsync()) {
				player.processCommand(binding.getCommand(), arg);
			}
		}
	}

	private static class BindingTask implements Runnable {
		private final Player player;
		private final String command;
		private final String[] arguments;

		BindingTask(Player player, String command, String... arguments) {
			this.player = player;
			this.command = command;
			this.arguments = arguments;
		}

		@Override
		public void run() {
			player.processCommand(command, arguments);
		}
	}

	private Screen getInputScreen() {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Client)) {
			throw new IllegalStateException("Cannot access ScreenStack in server mode.");
		}
		return ((Client) engine).getScreenStack().getInputScreen();
	}

	public void execute(float dt){
		for(InputExecutor executor : inputExecutors){
			SpoutSceneComponent sc = (SpoutSceneComponent) ((Client)Spout.getEngine()).getPlayer().getScene();
			executor.execute(dt, sc.getLiveTransform());
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

	public void onClientStart() {
		if (inputExecutors.size() == 0) {
			Spout.warn("No input executor found, using fallback input");
			addInputExecutor(new FallbackInputExecutor());
		}
	}

	private static class FallbackInputExecutor implements InputExecutor{
		@Override
		public void execute(float dt, Transform playerTransform) {

			final Client client = (Client) Spout.getEngine();
			final PlayerInputState state = client.getPlayer().input();
			final float speed = 50;
			final Vector3 motion;
			//TODO This needs to be an enum, this is hideous (shame on RoyAwesome)
			if (state.getForward()) {
				motion = playerTransform.forwardVector().multiply(speed * -dt);
			} else if (state.getBackward()) {
				motion = playerTransform.forwardVector().multiply(speed * dt);
			} else if (state.getLeft()) {
				motion = playerTransform.rightVector().multiply(speed * -dt); //TODO getLeftVector
			} else if (state.getRight()) {
				motion = playerTransform.rightVector().multiply(speed * dt);
			} else if (state.getJump()) {
				motion = playerTransform.upVector().multiply(speed * dt);
			} else if (state.getCrouch()) {
				motion = playerTransform.upVector().multiply(speed * -dt);
			} else {
				return;
			}

			playerTransform.translateAndSetRotation(motion, QuaternionMath.rotation(state.pitch(), state.yaw(), playerTransform.getRotation().getRoll()));
			client.getPlayer().getScene().setTransform(playerTransform);
		}
	}
}
