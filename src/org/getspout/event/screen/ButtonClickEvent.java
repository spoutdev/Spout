package org.getspout.event.screen;

import org.getspout.gui.Button;
import org.getspout.gui.Screen;
import org.getspout.player.ContribPlayer;

public class ButtonClickEvent extends ScreenEvent{

	protected Button control;
	public ButtonClickEvent(ContribPlayer player, Screen screen, Button control) {
		super("ButtonClickEvent", player, screen);
		this.control = control;
	}
	
	public Button getButton() {
		return control;
	}
}
