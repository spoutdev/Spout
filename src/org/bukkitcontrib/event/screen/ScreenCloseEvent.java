package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;

public class ScreenCloseEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenCloseEvent(Screen screen) {
		super("ScreenCloseEvent", screen);
	}
}
