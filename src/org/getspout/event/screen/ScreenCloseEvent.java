package org.getspout.event.screen;

import org.getspout.gui.Screen;
import org.getspout.player.ContribPlayer;

public class ScreenCloseEvent extends ScreenEvent{
	protected Screen screen;
	protected boolean cancel = false;
	public ScreenCloseEvent(ContribPlayer player, Screen screen) {
		super("ScreenCloseEvent", player, screen);
	}
}
