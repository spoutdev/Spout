package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Button;
import org.bukkitcontrib.gui.Screen;

public class ButtonClickEvent extends ScreenEvent{

	protected Button control;
	public ButtonClickEvent(Screen screen, Button control) {
		super("ButtonClickEvent", screen);
		this.control = control;
	}
	
	public Button getButton() {
		return control;
	}
}
