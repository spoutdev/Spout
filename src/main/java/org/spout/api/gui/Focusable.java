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

import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.math.IntVector2;

/**
 * Represents something that can be focused on.
 */
public interface Focusable {
	/**
	 * Returns false if this element should be skipped over when changing the
	 * focus.
	 *
	 * @return false if this element should be skipped over
	 */
	public boolean canFocus();

	/**
	 * Returns true if the widget has focus.
	 *
	 * @return true if widget has focus
	 */
	public boolean isFocused();

	/**
	 * Called when this element gains focus on a screen.
	 *
	 * @param reason for focus
	 */
	public void onFocus(FocusReason reason);

	/**
	 * Called when this loses focus.
	 */
	public void onBlur();

	/**
	 * Called when this element is clicked with a mouse button.
	 *
	 * @param event of click
	 */
	public void onClick(PlayerClickEvent event);

	/**
	 * Called when this is focused and a key is pressed or released
	 *
	 * @param event of key
	 */
	public void onKey(PlayerKeyEvent event);

	/**
	 * Called when this is focused and the mouse is moved.
	 *
	 * @param prev previous location of cursor in pixels
	 * @param pos new location of cursor in pixels
	 * @param hovered true if the cursor is hovering over this element
	 */
	public void onMouseMoved(IntVector2 prev, IntVector2 pos, boolean hovered);
}
