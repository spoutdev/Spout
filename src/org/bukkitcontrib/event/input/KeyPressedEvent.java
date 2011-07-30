package org.bukkitcontrib.event.input;

import org.bukkit.event.Event;
import org.bukkitcontrib.gui.ScreenType;
import org.bukkitcontrib.keyboard.Keyboard;
import org.bukkitcontrib.player.ContribPlayer;

public class KeyPressedEvent extends Event{
	private ContribPlayer player;
	private Keyboard key;
	private ScreenType screenType;
	public KeyPressedEvent(int keyPress, ContribPlayer player, ScreenType type) {
		super("KeyPressedEvent");
		this.player = player;
		this.key = Keyboard.getKey(keyPress);
		this.screenType = type;
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
