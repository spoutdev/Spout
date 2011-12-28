package org.getspout.api.protocol.bootstrap.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Protocol;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.bootstrap.msg.BootstrapIdentificationMessage;

public class BootstrapIdentificationMessageHandler extends MessageHandler<BootstrapIdentificationMessage> {
		@Override
		public void handle(Session session, Player player, BootstrapIdentificationMessage message) {
			session.setProtocol(Protocol.getProtocol(message.getSeed()));
		}
}
