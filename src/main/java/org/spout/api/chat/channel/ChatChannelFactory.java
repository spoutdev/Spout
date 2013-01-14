package org.spout.api.chat.channel;

import org.spout.api.command.CommandSource;

/**
 * A factory to create new chat channel instances
 */
public interface ChatChannelFactory {
	/**
	 * Returns a ChatChannel instance to be used as the channel for {@code source}.
	 * Does not necessarily have to be a new instance of a channel object,
	 * as channel objects can be shared by multiple users.
	 *
	 * @param source The source the resulting channel will be attached to
	 * @return The new channel object
	 */
	public ChatChannel create(CommandSource source);
}
