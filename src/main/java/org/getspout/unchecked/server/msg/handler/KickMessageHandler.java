package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.KickMessage;

public final class KickMessageHandler extends MessageHandler<KickMessage> {
	@Override
	public void handle(Session session, Player player, KickMessage message) {
		//session.disconnect("Goodbye!", true);
	}
}
