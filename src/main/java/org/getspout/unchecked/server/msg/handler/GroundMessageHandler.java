package org.getspout.unchecked.server.msg.handler;

import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.msg.GroundMessage;
import org.getspout.unchecked.server.net.Session;

public class GroundMessageHandler extends MessageHandler<GroundMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, GroundMessage message) {
		if (player != null) {
			player.setOnGround(message.isOnGround());
		}
	}
}
