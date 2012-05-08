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

import java.awt.Point;

import org.spout.api.gui.layout.FreeLayout;
import org.spout.api.gui.widget.AbstractWidget;
import org.spout.api.keyboard.Keyboard;

public class GenericScreen extends AbstractWidget implements Screen {
	
	private Control focussedControl;
	private Layout layout;

	public GenericScreen() {
		setLayout(new FreeLayout());
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		getLayout().onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		getLayout().onMouseMove(from, to);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		getLayout().onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		if (focussedControl != null) {
			focussedControl.onKeyPress(key);
		}
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		if (focussedControl != null) {
			focussedControl.onKeyRelease(key);
		}
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public Container setLayout(Layout layout) {
		this.layout = layout;
		if (layout != null) {
			layout.setParent(this);
		}
		return this;
	}

	@Override
	public void render() {
		getLayout().render();
	}

	@Override
	public Screen setFocussedControl(Control control) {
		this.focussedControl = control;
		return this;
	}

	@Override
	public Control getFocussedControl() {
		return focussedControl;
	}

	@Override
	public Screen getScreen() {
		return this;
	}

	@Override
	public Widget setScreen(Screen screen) {
		return this;
	}

	@Override
	public void onTick(float dt) {
		getLayout().onTick(dt);
	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.SCREEN;
	}

	@Override
	public ScreenType getScreenType() {
		return ScreenType.GENERICSCREEN;
	}
}
