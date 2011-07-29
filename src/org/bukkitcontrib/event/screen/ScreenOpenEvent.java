package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;
import org.bukkitcontrib.player.ContribPlayer

public class ScreenOpenEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenOpenEvent(ContribPlayer player, Screen screen) {
		super("ScreenOpenEvent", player, screen);
	}
}
