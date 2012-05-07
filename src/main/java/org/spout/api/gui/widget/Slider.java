package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.MouseButton;
import org.spout.api.gui.WidgetType;
import org.spout.api.gui.widget.AbstractControl;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.plugin.Plugin;

public class Slider extends AbstractControl implements RangedWidget<Integer> {

	private int min, max, value;
	
	public Slider(Plugin plugin) {
		super(plugin);
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
		if(min >= max) {
			throw new IllegalStateException("min must be smaller than max");
		}
		this.min = min;
		this.max = max;
		return this;
	}

	@Override
	public Slider setValue(Integer value) {
		if(value < min || value > max) {
			throw new IllegalStateException("value must be inside the range");
		}
		this.value = value;
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
