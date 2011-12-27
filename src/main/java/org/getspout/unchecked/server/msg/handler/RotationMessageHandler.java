package org.getspout.unchecked.server.msg.handler;

import org.bukkit.Location;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.RotationMessage;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {
	@Override
	public void handle(Session session, Player player, RotationMessage message) {
		/*if (player == null) {
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
		*/
	}
}
