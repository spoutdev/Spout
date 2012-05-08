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
package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.Control;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.MouseButton;
import org.spout.api.keyboard.Keyboard;

public abstract class AbstractControl extends AbstractWidget implements Control {
	private boolean enabled = true;

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		setFocus(FocusReason.MOUSE_CLICKED);
	}

	@Override
	public void onMouseMove(Point from, Point to) {

	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {

	}

	@Override
	public void onKeyPress(Keyboard key) {

	}

	@Override
	public void onKeyRelease(Keyboard key) {

	}

	@Override
	public boolean setFocus() {
		return setFocus(FocusReason.GENERIC_REASON);
	}

	@Override
	public boolean setFocus(FocusReason reason) {
		getScreen().setFocussedControl(this);
		return true;
	}

	@Override
	public boolean hasFocus() {
		return getScreen().getFocussedControl() == this;
	}

	@Override
	public Control setEnabled(boolean enable) {
		this.enabled = enable;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
