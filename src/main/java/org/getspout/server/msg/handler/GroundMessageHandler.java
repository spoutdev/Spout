package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.GroundMessage;
import org.getspout.server.net.Session;

public class GroundMessageHandler extends MessageHandler<GroundMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, GroundMessage message) {
		if (player != null) {
			player.setOnGround(message.isOnGround());
		}
	}
}
