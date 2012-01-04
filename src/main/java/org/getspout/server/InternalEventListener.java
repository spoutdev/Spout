package org.getspout.server;

import org.getspout.api.Spout;
import org.getspout.api.event.EventHandler;
import org.getspout.api.event.Listener;
import org.getspout.api.event.Order;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.api.player.Player;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.net.SpoutSession;
import org.getspout.server.player.SpoutPlayer;

public class InternalEventListener implements Listener {
	private final SpoutServer server;

	public InternalEventListener(SpoutServer server) {
		this.server = server;
	}

	@EventHandler(event = PlayerConnectEvent.class, priority = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if(event.isCancelled()) return;
		//Create the player
		Player player = server.addPlayer(event.getPlayerName(), (SpoutSession)event.getSession());

		if (player != null) {
			Spout.getGame().getEventManager().callEvent(new PlayerJoinEvent(player));
		} else {
			event.getSession().disconnect("Player is already online");
		}
	}

	@EventHandler(event = PlayerJoinEvent.class, priority = Order.LATEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		SpoutPlayer p = (SpoutPlayer)event.getPlayer();

		if(server.rawGetAllOnlinePlayers().size() >= server.getMaxPlayers()) {
			//TODO Kick with message
			p.disconnect();
		}

		server.broadcastMessage(p.getName() + " Has Connected");
	}

}
