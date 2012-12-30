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

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.chat.ChatArguments;

/**
 * Handles responses sent to a conversation
 */
public abstract class ResponseHandler {
	private Conversation convo;

	/**
	 * Returns the conversation this ResponseHandler is currently attached to. This can change.
	 * @return
	 */
	public Conversation getConversation() {
		final Conversation convo = this.convo;
		if (convo == null) {
			throw new IllegalStateException("This response handler (" + getClass().getSimpleName() + ") is not attached to a Conversation!");
		}

		return convo;
	}

	void setConversation(Conversation convo) {
		this.convo = convo;
	}

	/**
	 * Called when this response handler is attached to its conversation,
	 * or when the conversation is attached to a player, whichever comes last
	 * .
	 * @see Conversation#setResponseHandler(ResponseHandler)
	 */
	public void onAttached() {}

	/**
	 * Called when the player the Conversation is attached to
	 * leaves the conversation while this response handler is attached
	 */
	public void onLeave() {}

	/**
	 * Handle a message sent by the player this conversation is attached to
	 *
	 * @param message The message
	 */
	public abstract void onInput(ChatArguments message);
}

