package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.notch.msg.HandshakeMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;
import org.getspout.unchecked.server.net.Session.State;

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
