package org.getspout.api.protocol.bootstrap.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.bootstrap.msg.BootstrapPingMessage;

public class BootstrapPingMessageHandler extends MessageHandler<BootstrapPingMessage> {

	@Override
	public void handle(Session session, Player player, BootstrapPingMessage message) {
		System.out.println("Ping message received");
	}
	
}
