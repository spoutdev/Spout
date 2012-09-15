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
package org.spout.api.component.components;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.TextChatChannel;
import org.spout.api.entity.Player;
import org.spout.api.util.Named;

/**
 * TextChatComponent adds the ComponentHolder to the defaultChatChannel and the consoleChatChannel by default.
 * The ComponentHolder will talk in the defaultChatChannel by default. If the ComponentHolder is a Player the name
 * of that player will  be used by default to begin the messages.
 */
public class TextChatComponent extends EntityComponent implements Named {

	private TextChatChannel talkingInChannel;
	private ChatArguments entityName = new ChatArguments("Unknown");

	@Override
	public void onAttached(){
		if (getOwner() instanceof Player){
			entityName = new ChatArguments(((Player)getOwner()).getName());
		} else {
			entityName = new ChatArguments("Unknown");
		}
		Spout.getEngine().getDefaultTextChatChannel().join(this);
		talkingInChannel = Spout.getEngine().getDefaultTextChatChannel();
		Spout.getEngine().getConsoleTextChatChannel().join(this);
	}

	@Override
	public void onDetached(){
		talkingInChannel = null;
		for (TextChatChannel textChatChannel : Spout.getEngine().getChatChannelManager().getTextChatChannels()){
			if (textChatChannel != Spout.getEngine().getDefaultTextChatChannel()){
				textChatChannel.leave(this);
			}
		}
		entityName = new ChatArguments("Unknown");
	}

	/**
	 * Gets the TextChatChannel the ComponentHolder is talking in
	 *
	 * @return ChatChannels the ComponentHolder is talking in
	 */
	public TextChatChannel getTalkingChannel(){
		return talkingInChannel;
	}

	/**
	 * Let's the ComponentHolder start talking in a ChatChannel.
	 * If the ComponentHolder is not already listening to that ChatChannel it will be added.
	 *
	 * @param chatChannel to start talk in
	 */
	public void startTalking(TextChatChannel chatChannel){
		chatChannel.join(this);
		talkingInChannel = chatChannel;
	}

	/**
	 * Stops the ComponentHolder talking in a ChatChannel. The ComponentHolder will now start talking
	 * in the in the defaultChatChannel again.
	 *
	 * @param chatChannel to stop talking in active channel
	 * */
	public void stopTalking(TextChatChannel chatChannel){
		talkingInChannel = Spout.getEngine().getDefaultTextChatChannel();
	}

	/**
	 * Returns the name of the ComponentHolder
	 *
	 * @return componentHolder name as normal String
	 */
	public String getName(){
		return entityName.asString();
	}

	/**
	 * Returns the name of the ComponentHolder as ChatArguments
	 *
	 * @return componentHolder name as ChatArguments
	 */
	public ChatArguments getNameAsArguments(){
		return entityName;
	}

	/**
	 * Sets the name of the ComponentHolder
	 *
	 * @param name of ComponentHolder
	 */
	public void setName(ChatArguments name){
		entityName = name;
	}

	/**
	 * Let the ComponentHolder talk in the talking ChatChannel
	 *
	 * @param message which should be send via the talking ChatChannel
	 */
	public void talk(Object... message){
		talkingInChannel.broadcast(this, message);

	}
}
