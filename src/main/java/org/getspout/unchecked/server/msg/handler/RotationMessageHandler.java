package org.getspout.unchecked.server.msg.handler;

import org.bukkit.Location;
import org.getspout.api.protocol.notch.msg.RotationMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, RotationMessage message) {
		if (player == null) {
			return;
		}

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
