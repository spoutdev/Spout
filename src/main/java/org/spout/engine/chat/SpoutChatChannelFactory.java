package org.spout.engine.chat;

import org.spout.api.chat.channel.ChatChannel;
import org.spout.api.chat.channel.ChatChannelFactory;
import org.spout.api.chat.channel.PermissionChatChannel;
import org.spout.api.command.CommandSource;

/**
 * Factory that creates channels that resolve membership using a permission with the format:
 * <pre>spout.chat.receive.&lt;player name></pre>
 */
public class SpoutChatChannelFactory implements ChatChannelFactory {
	private static final String SPOUT_CHAT_PREFIX = "spout.chat.receive.";
	@Override
	public ChatChannel create(CommandSource source) {
		return new PermissionChatChannel(source.getName() + " Chat", SPOUT_CHAT_PREFIX + source.getName().toLowerCase());
	}
}
