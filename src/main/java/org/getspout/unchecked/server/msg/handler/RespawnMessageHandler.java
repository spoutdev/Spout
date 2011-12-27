package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.RespawnMessage;

public class RespawnMessageHandler extends MessageHandler<RespawnMessage> {
	@Override
	public void handle(Session session, Player player, RespawnMessage message) {
		//player.setHealth(20);
		//player.teleport(player.getWorld().getSpawnLocation());
	}
}
