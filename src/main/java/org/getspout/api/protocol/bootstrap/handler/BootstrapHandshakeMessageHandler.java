package org.getspout.api.protocol.bootstrap.handler;

import org.getspout.api.Commons;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.bootstrap.msg.BootstrapHandshakeMessage;

public class BootstrapHandshakeMessageHandler extends MessageHandler<BootstrapHandshakeMessage> {
	@Override
	public void handle(Session session, Player player, BootstrapHandshakeMessage message) {
		if (Commons.isSpout) {
			Session.State state = session.getState();
			if (state == Session.State.EXCHANGE_HANDSHAKE) {
				session.setState(Session.State.EXCHANGE_IDENTIFICATION);
				// TODO
				//if (session.getServer().getOnlineMode()) {
				//	session.send(new HandshakeMessage(session.getSessionId()));
				//} else {
					session.send(new BootstrapHandshakeMessage("-"));
				//}
			} else {
				session.disconnect("Handshake already exchanged.");
			}
		}
	}
}