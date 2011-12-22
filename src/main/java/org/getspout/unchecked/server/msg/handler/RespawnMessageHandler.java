package org.getspout.unchecked.server.msg.handler;

import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.msg.RespawnMessage;
import org.getspout.unchecked.server.net.Session;

public class RespawnMessageHandler extends MessageHandler<RespawnMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, RespawnMessage message) {
		player.setHealth(20);
		player.teleport(player.getWorld().getSpawnLocation());
	}
}
