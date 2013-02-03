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

import java.util.Iterator;
import java.util.LinkedList;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.gui.FullScreen;
import org.spout.api.gui.Screen;
import org.spout.api.gui.ScreenStack;
import org.spout.api.gui.Widget;
import org.spout.api.input.InputManager;
import org.spout.api.signal.SignalSubscriberObject;

public class SpoutScreenStack extends SignalSubscriberObject implements ScreenStack {
	private LinkedList<Screen> screens = new LinkedList<Screen>();
	private LinkedList<Screen> visibleScreens = new LinkedList<Screen>();
	/**
	 * The screen that gets input, can be null
	 */
	private Screen inputScreen = null;

	public SpoutScreenStack(FullScreen root) {
		screens.add(root);
		update();
	}

	public void openScreen(Screen screen) {
		synchronized (screens) {
			screens.add(screen);
		}
		update();
	}

	/**
	 * Updates all internal caches
	 */
	private void update() {
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
		}
		if (Spout.getEngine() instanceof Client) {
			Client engine = (Client) Spout.getEngine();
			InputManager input = engine.getInputManager();
			Iterator<Screen> iter = getVisibleScreens().descendingIterator();
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
	 * Gets an ordered list of visible screens
	 * The first item in the list is the bottom-most fullscreen, the last item in the list is the top-most fullscreen/popupscreen.
	 * @return
	 */
	public LinkedList<Screen> getVisibleScreens() {
		synchronized (visibleScreens) {
			return visibleScreens;
		}
	}

	@Override
	public void onTick(float dt) {
		for (Screen screen : getVisibleScreens()) {
			screen.tick(dt);
		}
	}

	/**
	 * Gets which screen takes input
	 * @return
	 */
	public Screen getInputScreen() {
		return inputScreen;
	}
	
	public Widget makeWidget() {
		return new SpoutWidget();
	}
}
