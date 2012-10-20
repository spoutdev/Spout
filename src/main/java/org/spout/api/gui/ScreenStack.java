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
package org.spout.api.gui;

import java.util.Iterator;
import java.util.LinkedList;

import org.spout.api.signal.SignalSubscriberObject;
import org.spout.api.tickable.Tickable;

public class ScreenStack extends SignalSubscriberObject implements Tickable {
	LinkedList<Screen> screens = new LinkedList<Screen>();
	LinkedList<Screen> visibleScreens = null;
	
	public ScreenStack(FullScreen root) {
		screens.add(root);
		dirty();
	}
	
	public void openScreen(Screen screen) {
		synchronized (screens) {
			screens.add(screen);
		}
		dirty();
	}
	
	public void closeTopScreen() {
		synchronized (screens) {
			screens.removeLast();
		}
		dirty();
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
		dirty();
	}
	
	/**
	 * Gets an ordered list of visible screens
	 * The first item in the list is the bottom-most fullscreen, the last item in the list is the top-most fullscreen/popupscreen.
	 * @return
	 */
	public LinkedList<Screen> getVisibleScreens() {
		synchronized (visibleScreens) {
			if (visibleScreens == null) {
				visibleScreens = new LinkedList<Screen>();
				
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
			return visibleScreens;
		}
	}
	
	private void dirty() {
		synchronized (visibleScreens) {
			visibleScreens = null;
		}
	}
	
	@Override
	public void onTick(float dt) {
		for (Screen screen:getVisibleScreens()) {
			screen.tick(dt);
		}
	}
}
