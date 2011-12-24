package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.notch.msg.KickMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

public final class KickMessageHandler extends MessageHandler<KickMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, KickMessage message) {
		session.disconnect("Goodbye!", true);
	}
}
