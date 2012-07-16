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

import org.spout.api.gui.MouseButton;
import org.spout.api.gui.TextProperties;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.signal.Signal;

public abstract class AbstractButton extends AbstractControl implements Button {
	private TextProperties textProperties = new TextProperties();
	private String text;
	private boolean down = false;
	private boolean checked = false;
	private boolean checkable = false;
	private int timeout = -1;
	
	/**
	 * @signal clicked When button is clicked
	 * 
	 * @sarg none
	 */
	public static final Signal SIGNAL_CLICKED = new Signal("clicked");
	
	/**
	 * @signal toggled When the check state has been toggled. 
	 * 
	 * @sarg java.lang.Boolean The new state
	 */
	public static final Signal SIGNAL_TOGGLED = new Signal("toggled", Boolean.class);
	
	{
		registerSignal(SIGNAL_CLICKED);
		registerSignal(SIGNAL_TOGGLED);
	}

	public AbstractButton(String text) {
		setText("");
	}
	
	public AbstractButton() {
		this("");
	}

	@Override
	public TextProperties getTextProperties() {
		return textProperties;
	}

	@Override
	public Label setTextProperties(TextProperties p) {
		this.textProperties = p;
		return this;
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isDown() {
		return down;
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public Button setChecked(boolean check) {
		if (isCheckable()) {
			if(check != this.checked) {
				this.checked = check;
				emit("toggled", check);
			}
		}
		return this;
	}

	@Override
	public Button setCheckable(boolean checkable) {
		if (!checkable && checked) {
			setChecked(false);
		}
		this.checkable = checkable;
		return this;
	}

	@Override
	public boolean isCheckable() {
		return checkable;
	}

	@Override
	public Button click() {
		setChecked(!isChecked());
		emit("clicked");
		return this;
	}

	@Override
	public Button clickLong(int ticks) {
		down = true;
		timeout = ticks;
		return this;
	}

	@Override
	public void onTick(float dt) {
		if (timeout >= 0) {
			if (timeout == 0) {
				down = false;
				click();
			}
			timeout --; //if timeout == 0, this will be -1, so it returns into valid state after that...
		}
		super.onTick(dt);
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		if (button == MouseButton.LEFT_BUTTON) {
			down = true;
		}
		super.onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		if (!getGeometry().contains(to)) {
			down = false;
		}
		super.onMouseMove(from, to);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		if (button == MouseButton.LEFT_BUTTON && isDown()) {
			down = false;
			click();
		}
		super.onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		if (key == Keyboard.KEY_SPACE) {
			down = true;
		}
		if (key == Keyboard.KEY_RETURN) {
			click();
		}
		super.onKeyPress(key);
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		if (key == Keyboard.KEY_ESCAPE) {
			down = false;
		}
		if (key == Keyboard.KEY_SPACE && isDown()) {
			down = false;
			click();
		}
		super.onKeyRelease(key);
	}
}
