package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.PositionRotationMessage;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {
	@Override
	public void handle(Session session, Player player, PositionRotationMessage message) {
		/*if (player == null) {
			return;
		}

		float rot = (message.getRotation() - 90) % 360;
		if (rot < 0) {
			rot += 360.0;
		}
		PlayerMoveEvent event = EventFactory.onPlayerMove(player, player.getLocation(), new Location(player.getWorld(), message.getX(), message.getY(), message.getZ(), rot, message.getPitch()));

		if (event.isCancelled()) {
			return;
		}

		player.setRawLocation(event.getTo());
		*/
	}
}
