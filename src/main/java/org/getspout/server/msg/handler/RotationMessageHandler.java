package org.getspout.server.msg.handler;

import org.bukkit.Location;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.RotationMessage;
import org.getspout.server.net.Session;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, RotationMessage message) {
		if (player == null)
			return;

		Location loc = player.getLocation();
		loc.setYaw(message.getRotation());
		float rot = (message.getRotation() - 90) % 360;
		if (rot < 0) {
			rot += 360.0;
		}
		loc.setPitch(rot);
		player.setRawLocation(loc);
	}
}
