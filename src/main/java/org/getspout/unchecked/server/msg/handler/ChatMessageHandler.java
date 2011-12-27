package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.ChatMessage;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {
	@Override
	public void handle(Session session, Player player, ChatMessage message) {
		if (player == null) {
			return;
		}

		String text = message.getMessage();
		text = text.trim();

		/*if (text.length() > 100) {
			session.disconnect("Chat message is too long.");
		} else {
			player.chat(text);
		}*/
	}
}
