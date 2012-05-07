package org.spout.api.gui.widget;

import org.spout.api.gui.WidgetType;
import org.spout.api.plugin.Plugin;

public class PushButton extends AbstractButton {
	
	public PushButton(String text, Plugin plugin) {
		super(text, plugin);
		setCheckable(false);
	}
	
	public PushButton(Plugin plugin) {
		this("", plugin);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.PUSHBUTTON;
	}
	
}
