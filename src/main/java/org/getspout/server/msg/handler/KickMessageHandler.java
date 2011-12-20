package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.KickMessage;
import org.getspout.server.net.Session;

public final class KickMessageHandler extends MessageHandler<KickMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, KickMessage message) {
		session.disconnect("Goodbye!", true);
	}
}
