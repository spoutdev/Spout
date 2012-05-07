package org.spout.api.gui.widget;

import org.spout.api.gui.WidgetType;
import org.spout.api.plugin.Plugin;

public class CheckBox extends AbstractButton {
	
	public CheckBox(String text, Plugin plugin) {
		super(text, plugin);
		setCheckable(true);
	}

	public CheckBox(Plugin plugin) {
		this("", plugin);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.CHECKBOX;
	}

}
