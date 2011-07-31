package org.getspout.event.screen;

import org.getspout.gui.Screen;
import org.getspout.player.ContribPlayer;

public class ScreenOpenEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenOpenEvent(ContribPlayer player, Screen screen) {
		super("ScreenOpenEvent", player, screen);
	}
}
