package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;
import org.bukkitcontrib.player.ContribPlayer

public class ScreenCloseEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenCloseEvent(ContribPlayer player, Screen screen) {
		super("ScreenCloseEvent", player, screen);
	}
}
