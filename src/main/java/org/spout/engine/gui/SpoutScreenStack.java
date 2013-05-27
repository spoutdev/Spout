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
package org.spout.engine.gui;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.gui.DebugHud;
import org.spout.api.gui.FullScreen;
import org.spout.api.gui.Screen;
import org.spout.api.gui.ScreenStack;
import org.spout.api.gui.Widget;
import org.spout.api.input.InputManager;
import org.spout.api.render.SpoutRenderMaterials;
import org.spout.api.signal.SignalSubscriberObject;

public class SpoutScreenStack extends SignalSubscriberObject implements ScreenStack {
	private LinkedList<Screen> screens = new LinkedList<Screen>();
	private LinkedList<Screen> visibleScreens = new LinkedList<Screen>();
	private final DevConsole console;
	private final DebugScreen debugScreen;
	/**
	 * The screen that gets input, can be null
	 */
	private Screen inputScreen = null;

	public SpoutScreenStack(FullScreen root) {
		screens.add(root);

		// Add the debug screen
		debugScreen = new DebugScreen();
		screens.add(debugScreen);
		
		// Add the dev console
		console = new DevConsole(SpoutRenderMaterials.DEFAULT_FONT);
		console.setDateFormat(new SimpleDateFormat("E HH:mm:ss"));
		screens.add(console);
		
		update();
	}

	public boolean isOpened(Screen screen) {
		synchronized (screens) {
			return screens.contains(screen);
		}
	}

	public void openScreen(Screen screen) {
		if (screen.getWidgets().isEmpty()) {
			throw new IllegalArgumentException("The specified screen doesn't have any widgets attached.");
		}

		synchronized (screens) {
			screens.add(screen);
		}
		update();
	}

	/**
	 * Updates all internal caches
	 */
	private void update() {
		Client engine = (Client) Spout.getEngine();
		synchronized (visibleScreens) {
			visibleScreens.clear();
			synchronized (screens) {
				Iterator<Screen> iter = screens.descendingIterator();
				Screen next = null;
				while (iter.hasNext()) {
					next = iter.next();
					visibleScreens.addFirst(next);
					if (next instanceof FullScreen) {
						break;
					}
				}
			}

			InputManager input = engine.getInputManager();
			Iterator<Screen> iter = visibleScreens.descendingIterator();
			Screen next;
			inputScreen = null;
			while (iter.hasNext()) {
				next = iter.next();
				if (next.takesInput()) {
					inputScreen = next;
					break;
				}
			}
			if (input != null) {
				input.setRedirected(inputScreen != null);
			}
		}
	}

	public void closeTopScreen() {
		synchronized (screens) {
			screens.removeLast();
		}
		update();
	}

	public void closeScreen(Screen screen) {
		synchronized (screens) {
			if (screen == screens.getFirst()) {
				Screen second = screens.get(1);
				if (!(second instanceof FullScreen)) {
					throw new IllegalStateException("The lowest screen must be instance of FullScreen!");
				}
			}
			screens.remove(screen);
		}
		update();
	}

	/**
	 * Gets a copy of the ordered list of visible screens
	 * The first item in the list is the bottom-most fullscreen, the last item in the list is the top-most fullscreen/popupscreen.
	 * @return copy of the visible screens, in order
	 */
	public LinkedList<Screen> getVisibleScreens() {
		synchronized (visibleScreens) {
			return new LinkedList<Screen>(visibleScreens);
		}
	}

	@Override
	public void onTick(float dt) {
		synchronized (visibleScreens) {
			for (Screen screen : visibleScreens) {
				screen.tick(dt);
			}
		}
	}

	/**
	 * Gets which screen takes input
	 * @return
	 */
	public Screen getInputScreen() {
		synchronized (visibleScreens) {
			return inputScreen;
		}
	}

	/**
	 * Get the debug screen
	 */
	public DebugHud getDebugHud() {
		return debugScreen;
	}

	/**
	 * Get the ingame developper's console
	 */
	public DevConsole getConsole() {
		return console;
	}

	public Widget createWidget() {
		return new SpoutWidget();
	}
}
