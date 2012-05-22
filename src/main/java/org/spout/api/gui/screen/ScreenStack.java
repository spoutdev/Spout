/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.gui.screen;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import org.spout.api.Tickable;
import org.spout.api.gui.KeyboardEventHandler;
import org.spout.api.gui.MouseButton;
import org.spout.api.gui.MouseEventHandler;
import org.spout.api.gui.Renderable;
import org.spout.api.gui.Screen;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.signal.Signal;
import org.spout.api.signal.SignalInterface;
import org.spout.api.signal.SignalSubscriberObject;
import org.spout.api.signal.SubscriberInterface;

public class ScreenStack extends SignalSubscriberObject implements Renderable, KeyboardEventHandler, MouseEventHandler, Tickable, SubscriberInterface, SignalInterface {
	/**
	 * Contains all attached screens in front-to-back order, that means, the topmost screen is the first element
	 */
	private LinkedList<Screen> screens = new LinkedList<Screen>();
	/**
	 * Contains the visible screens in back-to-front order, that means, the first element is the last screen you can see (and is also a fullscreen)
	 */
	private LinkedList<Screen> visibleScreens = new LinkedList<Screen>();
	
	private int width = -1, height = -1;
	
	/**
	 * @signal resized When the window that contains the screen has been resized
	 * 
	 * @sarg java.lang.Integer The new width
	 * @sarg java.lang.Integer The new height
	 */
	public static final Signal SIGNAL_RESIZED = new Signal("resized", Integer.class, Integer.class);
	
	{
		registerSignal(SIGNAL_RESIZED);
		updateScreenSize();
	}

	private void updateScreenSize() {
		width = Display.getWidth();
		height = Display.getHeight();
	}

	public ScreenStack(FullScreen mainScreen) {
		if (mainScreen == null) {
			throw new IllegalStateException("mainScreen must not be null");
		}
		screens.add(mainScreen);
		visibleScreens.add(mainScreen);
	}

	/**
	 * @return the first visible screen
	 */
	public Screen getFirstScreen() {
		return screens.getFirst();
	}

	/**
	 * @return all visible screens, ordered back-to-front, that means, the first element is the last screen you can see (and is also a FullScreen)
	 */
	public List<Screen> getVisibleScreens() {
		return Collections.unmodifiableList(visibleScreens);
	}

	/**
	 * Opens the given screen. If the screen already exists, it will push it to the front
	 * @param screen
	 */
	public void openScreen(Screen screen) {
		if (screen != getMainScreen()) {
			screens.remove(screen);
			screens.addFirst(screen);
			recalculate();
		} else {
			printMainScreenWarning("raise");
		}
	}

	private void printMainScreenWarning(String verb) {
		System.out.println("You can't " + verb + " the main screen. To replace it, call setMainScreen with a fullscreen as argument.");
	}

	/**
	 * Closes the topmost screen.
	 */
	public void closeFirstScreen() {
		if (screens.size() > 1) {
			screens.removeFirst();
			recalculate();
		} else {
			printMainScreenWarning("close");
		}
	}

	/**
	 * Closes the given screen.
	 * @param screen
	 */
	public void closeScreen(Screen screen) {
		if (screen != getMainScreen()) {
			screens.remove(screen);
			recalculate();
		} else {
			printMainScreenWarning("close");
		}
	}

	/**
	 * Sets the main screen to a new instance
	 * @param mainScreen the new main screen
	 */
	public void setMainScreen(FullScreen mainScreen) {
		if (mainScreen == null) {
			throw new IllegalStateException("Main Screen may not be null");
		}
		screens.set(screens.size() - 1, mainScreen);
	}

	/**
	 * Gets the main screen
	 * @return the main screen
	 */
	public FullScreen getMainScreen() {
		if (screens.getLast() instanceof FullScreen || screens.getLast() == null) {
			return (FullScreen) screens.getLast();
		} else {
			//Should never happen
			throw new IllegalStateException("Main Screen is not a FullScreen");
		}
	}

	private void recalculate() {
		visibleScreens.clear();
		for (Screen screen:screens) {
			visibleScreens.addLast(screen);
			if (screen instanceof FullScreen) {
				FullScreen fs = (FullScreen) screen;
				if (!fs.isTransparent()) {
					break;
				}
			}
		}
	}

	private Screen getHitScreen(Point position) {
		for (Screen screen:screens) {
			if (screen.getGeometry().contains(position)) {
				return screen;
			}
		}
		return null;
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		Screen screen = getHitScreen(position);
		if (screen != null) {
			Point screenPosition = new Point(position);
			screenPosition.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			getHitScreen(position).onMouseDown(screenPosition, button);
		}
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		Screen screen = getHitScreen(from);
		if (screen != null) {
			Point screenPositionFrom = new Point(from);
			Point screenPositionTo = new Point(to);
			screenPositionFrom.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			screenPositionTo.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			screen.onMouseMove(screenPositionFrom, screenPositionTo);
		}
		screen = getHitScreen(to);
		if (screen != null) {
			Point screenPositionFrom = new Point(from);
			Point screenPositionTo = new Point(to);
			screenPositionFrom.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			screenPositionTo.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			screen.onMouseMove(screenPositionFrom, screenPositionTo);
		}
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		Screen screen = getHitScreen(position);
		if (screen != null) {
			Point screenPosition = new Point(position);
			screenPosition.translate(-screen.getGeometry().x, -screen.getGeometry().y);
			getHitScreen(position).onMouseUp(screenPosition, button);
		}
	}

	@Override
	public void onKeyPress(Keyboard key) {
		getFirstScreen().onKeyPress(key);
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		getFirstScreen().onKeyRelease(key);
	}

	@Override
	public void render() {
		for (Screen screen:visibleScreens) {
			GL11.glTranslated(screen.getGeometry().x, screen.getGeometry().y, 0);
			GL11.glPushMatrix();
			screen.render();
			GL11.glPopMatrix();
		}
	}

	@Override
	public void onTick(float dt) {
		//Check for resize
		if(Display.getWidth() != width || Display.getHeight() != height) {
			updateScreenSize();
			emit(SIGNAL_RESIZED, width, height);
		}
		//Invisible screens don't have to be ticked
		for (Screen screen:visibleScreens) {
			screen.onTick(dt);
		}
	}
}
