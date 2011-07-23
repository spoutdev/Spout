package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;

public class ScreenOpenEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenOpenEvent(Screen screen) {
		super("ScreenOpenEvent", screen);
	}
}
