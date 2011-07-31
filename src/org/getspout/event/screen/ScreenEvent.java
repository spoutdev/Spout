package org.getspout.event.screen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.getspout.gui.Screen;
import org.getspout.player.ContribPlayer;

public abstract class ScreenEvent extends Event implements Cancellable{
	protected Screen screen;
	protected ContribPlayer player;
	protected boolean cancel = false;
	protected ScreenEvent(String name, ContribPlayer player, Screen screen) {
		super(name);
		this.screen = screen;
		this.player = player;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public ContribPlayer getPlayer() {
		return player;
	}
	
	public boolean isCancelled(){
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = true;
	}

}
