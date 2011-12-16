package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.RespawnMessage;
import org.getspout.server.net.Session;

public class RespawnMessageHandler extends MessageHandler<RespawnMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, RespawnMessage message) {
		player.setHealth(20);
		player.teleport(player.getWorld().getSpawnLocation());
	}
}
