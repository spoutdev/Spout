package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.notch.msg.ChatMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, ChatMessage message) {
		if (player == null) {
			return;
		}

		String text = message.getMessage();
		text = text.trim();

		if (text.length() > 100) {
			session.disconnect("Chat message is too long.");
		} else {
			player.chat(text);
		}
	}
}
