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
package org.spout.api.guix;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.ClientOnly;
import org.spout.api.Spout;
import org.spout.api.tickable.BasicTickable;

/**
 * Represents all screens to be updated and rendered every tick.
 */
public class ScreenStack extends BasicTickable {
	private final LinkedList<Screen> screens = new LinkedList<Screen>();
	private final GuiRenderer renderer;
	private Screen input;

	@ClientOnly
	public ScreenStack(GuiRenderer renderer) {
		if (!(Spout.getEngine() instanceof Client))
			throw new IllegalStateException("ScreenStack can only be instantiated in client mode.");
		this.renderer = renderer;
		updateInput();
	}

	/**
	 * Returns a {@link List} of opened screens.
	 *
	 * @return all opened screens
	 */
	public List<Screen> getScreens() {
		return Collections.unmodifiableList(screens);
	}

	/**
	 * Returns the renderer of the screen stack.
	 *
	 * @return renderer
	 */
	public GuiRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns the screen that all input is sent to
	 *
	 * @return input screen
	 */
	public Screen getInputScreen() {
		return input;
	}

	/**
	 * Opens the specified screen.
	 *
	 * @param screen to open
	 */
	public void open(Screen screen) {
		screens.add(screen);
		updateInput();
	}

	/**
	 * Closes the screen.
	 *
	 * @param screen to close
	 */
	public void close(Screen screen) {
		screens.remove(screen);
		updateInput();
	}

	/**
	 * Closes the top screen.
	 */
	public void closeTop() {
		screens.removeLast();
		updateInput();
	}

	/**
	 * Closes all currently opened screens.
	 */
	public void closeAll() {
		screens.clear();
		updateInput();
	}

	private synchronized void updateInput() {
		this.input = null;
		Iterator<Screen> iter = screens.descendingIterator();
		while (iter.hasNext()) {
			Screen next = iter.next();
			if (next.takesInput()) {
				this.input = next;
				break;
			}
		}
		((Client) Spout.getEngine()).getInputManager().setRedirected(this.input != null);
	}

	@Override
	public synchronized void onTick(float dt) {
		// update screens then render
		for (Screen screen : screens) {
			screen.tick(dt);
		}
		renderer.render(this);
	}

	@Override
	public boolean canTick() {
		return !screens.isEmpty();
	}
}
