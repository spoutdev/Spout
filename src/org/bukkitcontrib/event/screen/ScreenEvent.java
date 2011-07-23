package org.bukkitcontrib.event.screen;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkitcontrib.gui.Screen;

public abstract class ScreenEvent extends Event implements Cancellable{
	protected Screen screen;
	protected boolean cancel = false;
	protected ScreenEvent(String name, Screen screen) {
		super(name);
		this.screen = screen;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public boolean isCancelled(){
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = true;
	}

}
