package org.spout.api.gui.widget;

import org.spout.api.gui.WidgetType;
import org.spout.api.plugin.Plugin;

public class ProgressBar extends AbstractWidget implements RangedWidget<Integer> {

	private int min, max, value;
	
	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public ProgressBar setRange(Integer min, Integer max) {
		if(min >= max) {
			throw new IllegalStateException("min must be smaller than max");
		}
		this.min = min;
		this.max = max;
		return this;
	}

	@Override
	public ProgressBar setValue(Integer value) {
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
		return WidgetType.PROGRESSBAR;
	}

}
