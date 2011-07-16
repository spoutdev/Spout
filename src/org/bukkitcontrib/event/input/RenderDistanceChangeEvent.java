package org.bukkitcontrib.event.input;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkitcontrib.player.ContribPlayer;

public class RenderDistanceChangeEvent extends Event implements Cancellable{
	protected RenderDistance newView;
	protected ContribPlayer player;
	protected boolean cancel = false;
	public RenderDistanceChangeEvent(ContribPlayer player, RenderDistance newView) {
		super("RenderDistanceChangeEvent");
		this.player = player;
		this.newView = newView;
	}
	
	public RenderDistance getCurrentRenderDistance() {
		return player.getRenderDistance();
	}
	
	public RenderDistance getNewRenderDistance() {
		return newView;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
