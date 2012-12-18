/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.chat.channel;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatTemplate;
import org.spout.api.chat.Placeholder;
import org.spout.api.command.CommandSource;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.util.Named;

/**
 * ChatChannel define a way how Chat Messages are shown to the player and console. They can also be used to limit messages to a certain group of players.
 * Every joining player will be moved into the defaultChatChannel for talking and listening. A player can be removed from the defaultChatChannel for talking
 * but not for listening.<br/>
 */
public abstract class ChatChannel implements Named {
	public static final ChatTemplate DEFAULT_FORMAT = new ChatTemplate(new ChatArguments(PlayerChatEvent.MESSAGE));
	public static final Placeholder CHANNEL_NAME = new Placeholder("channel-name");

	private final String name;
	private ChatTemplate format = DEFAULT_FORMAT;

	public ChatChannel(String name) {
		this.name = name;
		Registry.addChannel(this);
	}

	/**
	 * Return the name of the ChatChannel
	 * @return the channel's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the members who should receive messages sent to this chat channel.
	 * The returned set must be an unmodifiable snapshot.
	 *
	 * @return The the set of {@link CommandSource CommandSources}
	 */
	public abstract Set<CommandSource> getReceivers();

	/**
	 * This implementation may be inefficient. Subclasses may override if they wish.
	 *
	 * @param source The source to check
	 * @return whether the source provided is a receiver
	 */
	public boolean isReceiver(CommandSource source) {
		return getReceivers().contains(source);
	}

	/**
	 * Broadcast to all the CommandSources of {@link #getReceivers()}.
	 *
	 * @param message The message to broadcast
	 */
	public void broadcastToReceivers(ChatArguments message) {
		ChatArguments formatted = applyPlaceholders(message);

		for (CommandSource source : getReceivers()) {
			source.sendMessage(formatted);
		}

	}

	private ChatArguments applyPlaceholders(ChatArguments message) {
		ChatArguments formatted = format.getArguments();
		// Placeholders
		formatted.setPlaceHolder(PlayerChatEvent.MESSAGE, message);
		if (formatted.hasPlaceholder(CHANNEL_NAME)) {
			formatted.setPlaceHolder(CHANNEL_NAME, new ChatArguments(CHANNEL_NAME));
		}

		return formatted;
	}

	/**
	 * Gets the format of the ChatChannel.
	 * The default format is "{MESSAGE}"
	 *
	 * @return format of ChatChannel
	 */
	public final ChatTemplate getFormat() {
		return format;
	}

	/**
	 * Sets the message's format to {@code format}. <br/>
	 * Verification is performed to make sure that the ChatArguments has the {@link org.spout.api.event.player.PlayerChatEvent#MESSAGE message} placeholder.
	 * The {@link org.spout.api.event.player.PlayerChatEvent#MESSAGE} will be used to hold the contents of messages broadcasted through this channel.
	 * The {@link #CHANNEL_NAME} placeholder will be replaced with the result of {@link #getName()} if present
	 *
	 * If verification of the format fails the format will not change.
	 *
	 * @param format The format to set.
	 * @return true if the format was valid, otherwise false.
	 */
	public boolean setFormat(ChatTemplate format) {
		Preconditions.checkNotNull(format);
		if (!format.getArguments().hasPlaceholder(PlayerChatEvent.MESSAGE)) {
			return false;
		}

		this.format = format;
		return true;
	}

	public static class Registry {
		private static final Set<ChatChannel> CHANNELS = Sets.newSetFromMap(new WeakHashMap<ChatChannel, Boolean>());

		private Registry() {
		}

		/**
		 * Return all ChatChannels that currently exist
		 * @return All currently existing channels
		 */
		public static Set<ChatChannel> getAllChannels() {
			return Collections.unmodifiableSet(CHANNELS);
		}

		public static void broadcastToAllReceivedChannels(CommandSource receiving, ChatArguments message) {
			Set<CommandSource> alreadyReceived = Sets.newHashSet();
			for (ChatChannel channel : getAllChannels()) {
				if (channel.isReceiver(receiving)) {
					Set<CommandSource> sendTo = channel.getReceivers();
					sendTo.removeAll(alreadyReceived);
					if (sendTo.size() > 0) {
						ChatArguments formatted = channel.applyPlaceholders(message);

						for (CommandSource source : sendTo) {
							source.sendMessage(formatted);
						}

						alreadyReceived.addAll(sendTo);
					}
				}
			}
		}

		/**
		 * Register a newly constructed channel.
		 * WILL ONLY BE CALLED FROM {@link ChatChannel} CONSTRUCTOR
		 *
		 * @param channel The channel to add
		 */
		private static void addChannel(ChatChannel channel) {
			CHANNELS.add(channel);
		}
	}
}
