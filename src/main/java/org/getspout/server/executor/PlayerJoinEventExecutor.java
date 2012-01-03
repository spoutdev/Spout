package org.getspout.server.executor;

import org.getspout.api.event.Event;
import org.getspout.api.event.EventException;
import org.getspout.api.event.EventExecutor;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.server.SpoutServer;
import org.getspout.server.player.SpoutPlayer;

public class PlayerJoinEventExecutor implements EventExecutor {
	SpoutServer server;
	public PlayerJoinEventExecutor(SpoutServer server){
		this.server = server;
	}
	public void execute(PlayerJoinEvent event){
		SpoutPlayer p = (SpoutPlayer)event.getPlayer();
		
		if(server.rawGetAllOnlinePlayers().size() >= server.getMaxPlayers()) {
			//TODO Kick with message
			p.disconnect();
		}
		
		server.rawGetAllOnlinePlayers().add(p);
		
		server.broadcastMessage(p.getName() + " Has Connected");
	}
	
	@Override
	public void execute(Event event) throws EventException {
		execute((PlayerJoinEvent)event);
	}

}
