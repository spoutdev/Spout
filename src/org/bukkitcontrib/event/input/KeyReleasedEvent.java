package org.bukkitcontrib.event.input;

import org.bukkit.event.Event;
import org.bukkitcontrib.gui.ScreenType;
import org.bukkitcontrib.keyboard.Keyboard;
import org.bukkitcontrib.player.ContribPlayer;

public class KeyReleasedEvent extends Event{
	private ContribPlayer player;
	private Keyboard key;
	private ScreenType screenType;
	public KeyReleasedEvent(int keyPress, ContribPlayer player, ScreenType screenType) {
		super("KeyReleasedEvent");
		this.player = player;
		this.key = Keyboard.getKey(keyPress);
		this.screenType = screenType;
	}
	
	public ContribPlayer getPlayer() {
		return player;
	}
	
	public Keyboard getKey() {
		return key;
	}
	
	public ScreenType getScreenType(){
		return screenType;
	}
}
