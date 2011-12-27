package org.getspout.api.protocol.bootstrap.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.bootstrap.msg.BootstrapHandshakeMessage;

public class BootstrapHandshakeMessageHandler extends MessageHandler<BootstrapHandshakeMessage> {
	@Override
	public void handle(Session session, Player player, BootstrapHandshakeMessage message) {
		System.out.println("Handshake message received");
	}
}