package org.getspout.event.input;

import org.bukkit.event.Event;
import org.getspout.keyboard.Keyboard;
import org.getspout.player.ContribPlayer;

public class KeyReleasedEvent extends Event{
	private ContribPlayer player;
	private Keyboard key;
	public KeyReleasedEvent(int keyPress, ContribPlayer player) {
		super("KeyReleasedEvent");
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
