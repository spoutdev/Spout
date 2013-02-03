/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.gui;

import java.util.LinkedList;

import org.spout.api.chat.console.Console;
import org.spout.api.signal.SignalInterface;
import org.spout.api.signal.SubscriberInterface;
import org.spout.api.tickable.Tickable;

public interface ScreenStack extends Tickable, SubscriberInterface, SignalInterface {
	
	public boolean isOpen(Screen screen);
	
	public void openScreen(Screen screen);

	public void closeTopScreen();

	public void closeScreen(Screen screen);

	/**
	 * Gets an ordered list of visible screens
	 * The first item in the list is the bottom-most fullscreen, the last item in the list is the top-most fullscreen/popupscreen.
	 * @return
	 */
	public LinkedList<Screen> getVisibleScreens();

	/**
	 * Gets which screen takes input
	 * @return Screen
	 */
	public Screen getInputScreen();
	
	/**
	 * Get the debug screen
	 */
	public abstract DebugHUD getDebug();
	
	/**
	 * Get the ingame developper's console
	 */
	public abstract Console getConsole();
	
	/**
	 * Return a new widget instance
	 * @return Widget
	 */
	public abstract Widget createWidget();
}
