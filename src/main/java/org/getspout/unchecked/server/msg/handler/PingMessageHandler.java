package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.notch.msg.PingMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

public class PingMessageHandler extends MessageHandler<PingMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, PingMessage message) {
		if (session.getPingMessageId() == message.getPingId()) {
			session.pong();
		}
	}
}
