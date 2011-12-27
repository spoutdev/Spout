package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.PositionMessage;

public final class PositionMessageHandler extends MessageHandler<PositionMessage> {
	@Override
	public void handle(Session session, Player player, PositionMessage message) {
		/*if (player == null) {
			return;
		}

		PlayerMoveEvent event = EventFactory.onPlayerMove(player, player.getLocation(), new Location(player.getWorld(), message.getX(), message.getY(), message.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));

		if (event.isCancelled()) {
			return;
		}

		Location l = event.getTo();
		l.setYaw(player.getLocation().getYaw());
		l.setPitch(player.getLocation().getPitch());

		player.setRawLocation(l);
		*/
	}
}
