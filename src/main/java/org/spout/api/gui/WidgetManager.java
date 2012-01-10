package org.spout.api.gui;

public interface WidgetManager {
	public void sendWidgetUpdate(Widget widget);

	public void sendFocusUpdate(Control control, boolean focus);
}
