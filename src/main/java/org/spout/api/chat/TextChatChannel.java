/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.chat;

import java.util.Set;

import org.spout.api.component.components.TextChatComponent;

/**
 * TextChatChannel define a way how Chat Messages are shown to the player and console. They can also be used to limit messages to a certain group of players.
 * Every joining player will be moved into the defaultChatChannel for talking and listening. A player can be removed from the defaultChatChannel for talking
 * but not for listening.<br/>
 * To make sure that messages have a general style if the ChatChannel is not completely configured the settings of the defaultChatChannel will be used.
 */
public interface TextChatChannel {

	/**
	 * Gets the internal name of the ChatChannel
	 *
	 * @return the internal chatChannelName
	 */
	public String getTextChatChannelName();

	/**
	 * Adds an TextChatComponent to the ChatChannel
	 *
	 * @param listener to add to the ChatChannel
	 * @return true if the TextChatComponent was added to the ChatChannel
	 */
	public boolean join(TextChatComponent listener);

	/**
	 * Removes an TextChatComponent from the ChatChannel. If this is the defaultChatChannel a
	 * @see DefaultChannelRemovalException is thrown.
	 *
	 * @param listener which is removed from the ChatChannel
	 * @return false if the TextChatComponent doesn't exist anymore
	 */
	public boolean leave(TextChatComponent listener);

	/**
	 * Returns a set of Listeners for this ChatChannel
	 *
	 * @return set of Listeners
	 */
	public Set<TextChatComponent> getListeners();

	/**
	 * Sets the visible ChatChannel name
	 *
	 * @param name which is displayed to the user as ChatChannel name
	 */
	public void setName(String name);

	/**
	 * Gets the visible ChatChannel name
	 *
	 * @return name which is displayed to the user as ChatChannel name
	 */
	public String getName();

	/**
	 * Gets the format of the ChatChannel.
	 *
	 * @return format of ChatChannel
	 */
	public ChatTemplate getFormat();

	/**
	 * Sets the message's format to {@code format}. <br/>
	 * Verification is performed to make sure that the ChatArguments has the {@link #MESSAGE message} placeholder.
	 * Additional placeholders are {@link #NAME name} for the player name,
	 * and {@link #CHANNELNAME channel-name}.<br/>
	 *
	 * If verification of the format fails the format will not change.
	 *
	 * @param format The format to set.
	 * @return true if the format was valid, otherwise false.
	 */
	public boolean setFormat(ChatArguments format);

	/**
	 * Broadcasts formatted message for this ChatChannel without the talkers name
	 *
	 * @param message to format
	 */
	public void broadcast(Object... message);

	/**
	 * Broadcasts formatted message for this ChatChannel with the talkers name
	 *
	 * @param message to format
	 */
	public void broadcast(TextChatComponent talker, Object... message);
}
