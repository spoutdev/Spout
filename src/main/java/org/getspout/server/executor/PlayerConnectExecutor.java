package org.getspout.server.executor;

import org.getspout.api.Spout;
import org.getspout.api.event.Event;
import org.getspout.api.event.EventException;
import org.getspout.api.event.EventExecutor;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.api.player.Player;
import org.getspout.server.SpoutServer;
import org.getspout.server.net.SpoutSession;

public class PlayerConnectExecutor implements EventExecutor {
	
	private final SpoutServer server;
	
	public PlayerConnectExecutor(SpoutServer server) {
		this.server = server;
	}

	public void execute(PlayerConnectEvent event) throws EventException {
		if(event.isCancelled()) return;
		//Create the player
		Player player = server.addPlayer(event.getPlayerName(), (SpoutSession)event.getSession());
		
		if (player != null) {
			Spout.getGame().getEventManager().callEvent(new PlayerJoinEvent(player));
		} else {
			event.getSession().disconnect("Player is already online");
		}
		
	}

	public void execute(Event event) throws EventException {
		if (event instanceof PlayerConnectEvent) {
			execute((PlayerConnectEvent)event);
		}
	}
}
