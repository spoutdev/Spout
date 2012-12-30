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
package org.spout.api.chat.conversation;

import java.util.Collections;
import java.util.Set;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.channel.ChatChannel;
import org.spout.api.command.CommandSource;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.map.DefaultedHashMap;
import org.spout.api.map.DefaultedMap;

/**
 * Tracks the state of a conversation between a player and a bot or other player
 */
public class Conversation extends ChatChannel {
	private ConversationState state = ConversationState.CREATED;
	private DefaultedMap<Object> context = new DefaultedHashMap<Object>();
	private final CommandSource participant;
	private ChatChannel previousChannel;
	private ResponseHandler responseHandler;

	public Conversation(String name, CommandSource participant) {
		super(name);
		this.participant = participant;
	}

	@Override
	public Set<CommandSource> getReceivers() {
		return Collections.singleton(getParticipant());
	}

	@Override
	public boolean isReceiver(CommandSource source) {
		return getParticipant().equals(source);
	}

	public CommandSource getParticipant() {
		return participant;
	}

	@Override
	public void broadcastToReceivers(CommandSource source, ChatArguments message) {
		super.broadcastToReceivers(source, message); // Broadcast this message
		if (source == getParticipant() && getState() == ConversationState.ACTIVE && getResponseHandler() != null) {
			// Uunwrap any external formatting from the passed message, so that we don't get any extra stuff (player name for example)
			ChatArguments realArguments = message;
			while (realArguments != null && realArguments.hasPlaceholder(PlayerChatEvent.MESSAGE)) {
				realArguments = realArguments.getPlaceholder(PlayerChatEvent.MESSAGE);
			}

			if (realArguments != null) {
				getResponseHandler().onInput(realArguments);
			}
		}
	}

	public final Conversation setResponseHandler(ResponseHandler handler) {
		this.responseHandler = handler;
		handler.setConversation(this);
		if (getState() == ConversationState.ACTIVE) {
			handler.onAttached();
		}
		return this;
	}

	public final ResponseHandler getResponseHandler() {
		return this.responseHandler;
	}

	public final ConversationState getState() {
		return state;
	}

	@Override
	public void onAttachTo(CommandSource source) {
		if (source == getParticipant() && getState() == ConversationState.CREATED) {
			if (!setState(ConversationState.ACTIVE)) {
				source.setActiveChannel(source.getActiveChannel());
			} else {
				if (source.getActiveChannel() != this) {
					previousChannel = source.getActiveChannel();
				}
			}
		}
	}

	@Override
	public void onDetachedFrom(CommandSource source) {
		if (source == getParticipant() && getState() == ConversationState.ACTIVE) {
			setState(ConversationState.FINISHED);
		}
	}

	public boolean finish() {
		if (setState(ConversationState.FINISHED)) {
			getParticipant().setActiveChannel(previousChannel);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the context of this conversation.
	 * This is an in-memory store that can be used to store custom state information and other data
	 *
	 * @return The conversation's context
	 */
	public DefaultedMap<Object> getContext() {
		return context;
	}

	/**
	 * Change the state of this conversation. This is done automatically when the conversation is attached or detached.
	 *
	 * @param state The new state
	 * @return if successful
	 */
	private boolean setState(ConversationState state) {
		if (!state.appliesTo(this)) {
			return false;
		}

		state.onAppliedTo(this);
		this.state = state;

		return true;
	}
}
