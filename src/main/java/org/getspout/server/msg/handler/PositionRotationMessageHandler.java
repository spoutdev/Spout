package org.getspout.server.msg.handler;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import org.getspout.server.EventFactory;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.PositionRotationMessage;
import org.getspout.server.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, PositionRotationMessage message) {
		if (player == null) {
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
	}
}
