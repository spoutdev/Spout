/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.chat;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.spout.api.Spout;
import org.spout.api.chat.ChatChannelManager;
import org.spout.api.chat.DefaultChannelRemovalException;
import org.spout.api.chat.TextChatChannel;
import org.spout.api.component.components.TextChatComponent;
import org.spout.api.plugin.CommonPlugin;

/**
 * Manages the ChatChannels.
 */
public class SpoutChatChannelManager implements ChatChannelManager {

	private final ConcurrentSkipListSet<TextChatChannel> textChatChannels = new ConcurrentSkipListSet<TextChatChannel>();


	@Override
	public Set<TextChatChannel> getTextChatChannels() {
		return textChatChannels;
	}

	@Override
	public void removeTextChatChannel(TextChatChannel chatChannel) {
		if (chatChannel.equals(Spout.getEngine().getDefaultTextChatChannel())){
			throw new DefaultChannelRemovalException();
		}
		for (TextChatComponent listener :chatChannel.getListeners()){
			listener.stopTalking(chatChannel);
		}
		textChatChannels.remove(chatChannel);
	}

	@Override
	public void addTextChatChannel(TextChatChannel chatChannel) {
		textChatChannels.add(chatChannel);
	}

	@Override
	public TextChatChannel getTextChatChannel(CommonPlugin plugin, String internalName) {
		return getTextChatChannel(plugin.getName() + ":" + internalName);
	}

	@Override
	public TextChatChannel getTextChatChannel(String textChatChannelName) {
		for (TextChatChannel textChatChannel: textChatChannels){
			if (textChatChannel.getTextChatChannelName().equalsIgnoreCase(textChatChannelName)){
				return textChatChannel;
			}
		}
		return null;
	}
}
