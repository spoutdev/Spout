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

import org.spout.api.plugin.CommonPlugin;

/**
 * Manages the ChatChannels.
 */
public interface ChatChannelManager {

	/**
	 * Gets all the TextChatChannels registered with the ChannelManager
	 *
	 * @return Set of TextChatChannels
	 */
	public Set<TextChatChannel> getTextChatChannels();

	/**
	 * Removes a TextChatChannel from the TextChatChannel lists.
	 *
	 * @param chatChannel which should be removed
	 */
	public void removeTextChatChannel (TextChatChannel chatChannel);

	/**
	 * Adds a TextChatChannel to the TextChatChannel list
	 *
	 * @param chatChannel which should be added
	 */
	public void addTextChatChannel(TextChatChannel chatChannel);

	/**
	 * Gets the TextChatChannel of a Plugin with the given internal name
	 *
	 * @param plugin which generated the ChatChannel
	 * @param internalName which was being used to generate the TextChatChannel
	 * @return TextChatChannel which was being searched for, may be NULL
	 */
	public TextChatChannel getTextChatChannel(CommonPlugin plugin, String internalName);

	/**
	 * Gets the TextChatChannel of a Plugin with the given internal name
	 *
	 * @param textChatChannelName which was being used to generate the TextChatChannel
	 * @return TextChatChannel which was being searched for, may be NULL
	 */
	public TextChatChannel getTextChatChannel(String textChatChannelName);
}

