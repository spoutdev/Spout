package org.getspout.api.event.player;

import org.getspout.api.event.Event;
import org.getspout.api.event.HandlerList;
import org.getspout.api.protocol.Session;

public class PlayerConnectEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();

	private final Session s;
	private final String playerName;
	
	public PlayerConnectEvent(Session s, String playerName){
		this.s = s;
		this.playerName = playerName;
	}
	
	public Session getSession() {
		return s;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
