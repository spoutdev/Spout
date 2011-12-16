package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.HandshakeMessage;
import org.getspout.server.net.Session;
import org.getspout.server.net.Session.State;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, HandshakeMessage message) {
		Session.State state = session.getState();
		if (state == Session.State.EXCHANGE_HANDSHAKE) {
			session.setState(State.EXCHANGE_IDENTIFICATION);
			if (session.getServer().getOnlineMode()) {
				session.send(new HandshakeMessage(session.getSessionId()));
			} else {
				session.send(new HandshakeMessage("-"));
			}
		} else {
			session.disconnect("Handshake already exchanged.");
		}
	}
}
