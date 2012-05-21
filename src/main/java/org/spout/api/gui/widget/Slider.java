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
import org.spout.api.gui.WidgetType;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.signal.Signal;

public class Slider extends AbstractControl implements RangedWidget<Integer> {

	private int min, max, value;
	
	/**
	 * Emitted when the slider was moved. 
	 * First argument contains the new position
	 */
	public static final Signal SIGNAL_SLIDER_MOVED = new Signal("sliderMoved", Integer.class);
	
	{
		registerSignal(SIGNAL_SLIDER_MOVED);
	}
	
	public Slider() {
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		// TODO Auto-generated method stub
		super.onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		// TODO Auto-generated method stub
		super.onMouseMove(from, to);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		// TODO Auto-generated method stub
		super.onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		// TODO Auto-generated method stub
		super.onKeyPress(key);
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		// TODO Auto-generated method stub
		super.onKeyRelease(key);
	}

	@Override
	public Slider setRange(Integer min, Integer max) {
		if (min >= max) {
			throw new IllegalStateException("min must be smaller than max");
		}
		this.min = min;
		this.max = max;
		return this;
	}

	@Override
	public Slider setValue(Integer value) {
		if (value < min || value > max) {
			throw new IllegalStateException("value must be inside the range");
		}
		this.value = value;
		emit("sliderMoved", value);
		return this;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer getRangeMin() {
		return min;
	}

	@Override
	public Integer getRangeMax() {
		return max;
	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.SLIDER;
	}

}
