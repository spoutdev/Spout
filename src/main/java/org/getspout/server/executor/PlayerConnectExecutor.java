package org.getspout.server.executor;

import org.getspout.api.Spout;
import org.getspout.api.event.Event;
import org.getspout.api.event.EventException;
import org.getspout.api.event.EventExecutor;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.player.SpoutPlayer;
import org.getspout.server.net.SpoutSession;

public class PlayerConnectExecutor implements EventExecutor {

	public void execute(PlayerConnectEvent event) throws EventException {
		if(event.isCancelled()) return;
		//Create the player
		SpoutPlayer player = new SpoutPlayer(event.getPlayerName());
		((SpoutSession)event.getSession()).setPlayer(player);
		player.setEntity(new SpoutEntity());
		
		Spout.getGame().getEventManager().callEvent(new PlayerJoinEvent(player));
		
	}

	public void execute(Event event) throws EventException {
		if (event instanceof PlayerConnectEvent) {
			execute((PlayerConnectEvent)event);
		}
	}
}
