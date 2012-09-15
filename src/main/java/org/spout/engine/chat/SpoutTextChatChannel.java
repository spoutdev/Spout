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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatChannelManager;
import org.spout.api.chat.ChatTemplate;
import org.spout.api.chat.DefaultChannelRemovalException;
import org.spout.api.chat.MissingPlaceholderException;
import org.spout.api.chat.Placeholder;
import org.spout.api.chat.TextChatChannel;
import org.spout.api.command.CommandSource;
import org.spout.api.component.components.TextChatComponent;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.util.Named;

import org.spout.engine.SpoutConfiguration;

public class SpoutTextChatChannel implements TextChatChannel, Named {
	private final String internalChatChannelName;
	private final ConcurrentSkipListSet<WeakReference<TextChatComponent>> channelListeners = new ConcurrentSkipListSet<WeakReference<TextChatComponent>>();
	private final ChatChannelManager chatChannelManager = Spout.getEngine().getChatChannelManager();
	private ChatArguments chatChannelName;
	public final Placeholder CHANNELNAME = new Placeholder("channel-name");
	public final Placeholder NAME = new Placeholder("name");
	public final Placeholder MESSAGE = new Placeholder("message");
	private ChatTemplate format = new ChatTemplate(new ChatArguments("<", NAME,"> ",MESSAGE));


	/**
	 * Create a Spout internal TextChatChannel with no player visible name, the format of the defaultChatChannel and an ChatChannelName of Spout:internalName
	 *
	 * @param engine the spout engine
	 * @param internalName of the TextChatChannel
	 */
	public SpoutTextChatChannel(Engine engine, String internalName) {
		this (engine, internalName, "");
	}

	/**
	 * Create a Spout internal TextChatChannel with a player visible name, the format of the defaultChatChannel and an ChatChannelName of Spout:internalName
	 *
	 * @param engine the spout engine
	 * @param internalName of the TextChatChannel
	 * @param channelName which is visible to the player
	 */
	public SpoutTextChatChannel(Engine engine, String internalName, String channelName){
		internalChatChannelName = "Spout:"+ internalName;
		chatChannelManager.addTextChatChannel(this);
		chatChannelName = new ChatArguments(channelName);
		if (Spout.getEngine().getDefaultTextChatChannel() == null) {
			validateFormat(ChatTemplate.fromFormatString(SpoutConfiguration.DEFAULT_CHATCHANNEL_FORMAT.getString()));
		} else {
			format = Spout.getEngine().getDefaultTextChatChannel().getFormat();
		}
	}

	/**
	 * Create a Spout internal TextChatChannel with a player visible name, a specific format and an ChatChannelName of Spout:internalName
	 *
	 * @param engine the spout engine
	 * @param internalName of the TextChatChannel
	 * @param channelName which is visible to the player
	 * @param format which is being used to display the message
	 */
	public SpoutTextChatChannel(Engine engine, String internalName, String channelName, ChatTemplate format){
		internalChatChannelName = "Spout:"+ internalName;
		chatChannelManager.addTextChatChannel(this);
		chatChannelName = new ChatArguments(channelName);
		validateFormat(format);
	}

	/**
	 * Create a plugin specific TextChatChannel with no player visible name, the format of the defaultChatChannel and a ChatChannelName of PluginName:internalName
	 *
	 * @param plugin which is creating the channel
	 * @param internalName of the TextChatChannel
	 */
	public SpoutTextChatChannel(CommonPlugin plugin, String internalName) {
		this(plugin, internalName, "");
	}

	/**
	 * Create a plugin TextChatChannel with an player visible name, the format of the defaultChatChannel and a ChatChannelName of PluginName:internalName
	 *
	 * @param plugin which is creating the channel
	 * @param internalName of the TextChatChannel
	 * @param channelName which is visible to the player
	 */
	public SpoutTextChatChannel(CommonPlugin plugin, String internalName, String channelName) {
		this(plugin, internalName, channelName, Spout.getEngine().getDefaultTextChatChannel().getFormat());
	}

	/**
	 * Create a plugin TextChatChannel with an user visible name, a specific format and a ChatChannelName of PluginName:internalName
	 *
	 * @param plugin which is creating the channel
	 * @param internalName of the TextChatChannel
	 * @param channelName which is visible to the player
	 * @param format which is being used to display the message
	 */
	public SpoutTextChatChannel(CommonPlugin plugin, String internalName, String channelName, ChatTemplate format) {
		internalChatChannelName = plugin.getName() + ":" + internalName;
		chatChannelManager.addTextChatChannel(this);
		chatChannelName = new ChatArguments(channelName);
		validateFormat(format);
	}

	/**
	 * Gets the format of the ChatChannel
	 *
	 * @return format of ChatChannel
	 */
	@Override
	public ChatTemplate getFormat() {
		return format;
	}

	/**
	 * Sets the message's format to {@code format}. <br/>
	 * Verification is performed to make sure that the ChatArguments has the {@link #MESSAGE message} placeholder.
	 * Additional placeholders are {@link #NAME name} for the name of the player,
	 * and {@link #CHANNELNAME channel-name}. <br/>
	 *
	 * If verification of the format fails the format will not change.
	 *
	 * @param format The format to set.
	 * @return true if the format was valid, otherwise false.
	 */
	@Override
	public boolean setFormat(ChatArguments format) {
		if (!(format.hasPlaceholder(MESSAGE))) {
			return false;
		}
		this.format = new ChatTemplate(format);
		return true;
	}

	@Override
	public void setName(String name) {
		chatChannelName = new ChatArguments(name);
	}

	@Override
	public String getName() {
		return chatChannelName.asString();
	}

	@Override
	public void broadcast(Object... message) {
		ChatArguments template = format.getArguments();
		template.setPlaceHolder(MESSAGE, new ChatArguments(message));
		if (template.hasPlaceholder(CHANNELNAME)){
			template.setPlaceHolder(CHANNELNAME, chatChannelName);
		}
		for (TextChatComponent listener : getListeners()){
			((CommandSource) listener).sendMessage(template);
		}
	}

	@Override
	public void broadcast(TextChatComponent talker, Object... message) {
		ChatArguments template = format.getArguments();
		template.setPlaceHolder(MESSAGE, new ChatArguments(message));
		if (template.hasPlaceholder(CHANNELNAME)){
			template.setPlaceHolder(CHANNELNAME, chatChannelName);
		}
		if (template.hasPlaceholder(NAME)){
			template.setPlaceHolder(NAME, talker.getNameAsArguments());
		}
		for (TextChatComponent listener : getListeners()){
			((CommandSource) listener).sendMessage(template);
		}
	}

	@Override
	public String getTextChatChannelName() {
		return internalChatChannelName;
	}

	@Override
	public boolean join(TextChatComponent listener) {
		return channelListeners.add(new WeakReference<TextChatComponent>(listener));
	}

	@Override
	public boolean leave(TextChatComponent listener){
		if (this == Spout.getEngine().getDefaultTextChatChannel()){
			throw new DefaultChannelRemovalException();
		}
		boolean success = false;
		for (Iterator<WeakReference<TextChatComponent>> iterator = channelListeners.iterator();
			 iterator.hasNext(); ) {
			WeakReference<TextChatComponent> weakReference = iterator.next();
			if (listener == weakReference.get()){
				iterator.remove();
				success = true;
			}
		}
		return success;
	}

	@Override
	public Set<TextChatComponent> getListeners() {
		Set<TextChatComponent> listeners = new HashSet<TextChatComponent>();
		for (WeakReference<TextChatComponent> listener : channelListeners) {
			if (listener.get() != null) {
				listeners.add(listener.get());
			}
		}
		return listeners;
	}

	/**
	 * Checks if SpoutConfiguration contains a valid ChatChannel format, if it is valid it
	 * will set the format..
	 *
	 * @throws MissingPlaceholderException
	 * @param chatTemplate to validate
	 */
	private void validateFormat(ChatTemplate chatTemplate){
		ChatArguments template = chatTemplate.getArguments();
		if (!(template.hasPlaceholder(MESSAGE))) {
			throw new MissingPlaceholderException("The " + MESSAGE + " Placeholder is missing for the " + internalChatChannelName + " ChatChannel.");
		}
		this.format = new ChatTemplate(template);
	}
}
