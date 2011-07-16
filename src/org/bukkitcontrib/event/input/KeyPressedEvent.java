package org.bukkitcontrib.event.input;

import org.bukkit.event.Event;
import org.bukkitcontrib.keyboard.Keyboard;
import org.bukkitcontrib.player.ContribPlayer;

public class KeyPressedEvent extends Event{
	private ContribPlayer player;
	private Keyboard key;
	public KeyPressedEvent(int keyPress, ContribPlayer player) {
		super("KeyPressedEvent");
		this.player = player;
		this.key = Keyboard.getKey(keyPress);
	}
	
	public ContribPlayer getPlayer() {
		return player;
	}
	
	public Keyboard getKey() {
		return key;
	}
}
