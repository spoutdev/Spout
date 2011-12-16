package org.getspout.server.msg.handler;

import java.util.logging.Level;

import org.bukkit.ChatColor;

import org.getspout.server.SpoutServer;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.ChatMessage;
import org.getspout.server.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, ChatMessage message) {
		if (player == null)
			return;

		String text = message.getMessage();
		text = text.trim();

		if (text.length() > 100) {
			session.disconnect("Chat message is too long.");
		} else {
			player.chat(text);
		}
	}
}
