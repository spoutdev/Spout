package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Button;
import org.bukkitcontrib.gui.Screen;
import org.bukkitcontrib.player.ContribPlayer;

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
