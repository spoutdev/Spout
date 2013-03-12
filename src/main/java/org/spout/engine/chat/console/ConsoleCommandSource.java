/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.chat.console;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.FormattedLogRecord;
import org.spout.api.chat.channel.ChatChannel;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.Command;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.event.server.PreCommandEvent;
import org.spout.api.geo.World;
import org.spout.api.lang.Locale;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;

/**
 * An implementation of CommandSource that sends to and receives from various consoles.
 */
public class ConsoleCommandSource implements CommandSource {
	private final AtomicReference<ChatChannel> activeChannel = new AtomicReference<ChatChannel>();
	private final SpoutEngine engine;
	private Locale preferredLocale = null;

	public ConsoleCommandSource(SpoutEngine engine) {
		this.engine = engine;
		activeChannel.set(engine.getChatChannelFactory().create(this));
	}

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public boolean sendMessage(Object... text) {
		return sendMessage(new ChatArguments(text));
	}

	@Override
	public void sendCommand(String command, ChatArguments arguments) {
		processCommand(command, arguments);
	}

	@Override
	public void processCommand(String commandName, ChatArguments arguments) {
		PreCommandEvent event = engine.getEventManager().callEvent(new PreCommandEvent(this, commandName, arguments));
		if (event.isCancelled()) {
			return;
		}
		commandName = event.getCommand();
		arguments = event.getArguments();

		Command command = engine.getRootCommand().getChild(commandName);
		if (command != null) {
			command.process(this, commandName, arguments, false);
		} else {
			sendMessage(ChatStyle.RED, "Unknown command: ", commandName);
		}
	}

	@Override
	public boolean sendMessage(ChatArguments message) {
		return sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Object... text) {
		return sendRawMessage(new ChatArguments(text));
	}

	@Override
	public boolean sendRawMessage(ChatArguments message) {
		engine.getLogger().log(new FormattedLogRecord(Level.INFO, message));
		return true;
	}

	@Override
	public boolean hasPermission(String node) {
		return true;
	}

	@Override
	public boolean isInGroup(String group) {
		return false;
	}

	@Override
	public boolean isInGroup(World world, String group) {
		return false;
	}

	@Override
	public String[] getGroups() {
		return new String[0];
	}

	@Override
	public String[] getGroups(World world) {
		return new String[0];
	}

	@Override
	public boolean hasPermission(World world, String node) {
		return true;
	}

	@Override
	public ValueHolder getData(String node) {
		return null;
	}

	@Override
	public ValueHolder getData(World world, String node) {
		return null;
	}

	@Override
	public boolean hasData(String node) {
		return false;
	}

	@Override
	public boolean hasData(World world, String node) {
		return false;
	}

	@Override
	public Locale getPreferredLocale() {
		if (preferredLocale == null) {
			preferredLocale = Locale.getByCode(SpoutConfiguration.DEFAULT_LANGUAGE.getString());
		}
		return preferredLocale;
	}

	public ChatChannel getActiveChannel() {
		return activeChannel.get();
	}

	public void setActiveChannel(ChatChannel chan) {
		Preconditions.checkNotNull(chan);
		chan.onAttachTo(this);
		this.activeChannel.getAndSet(chan).onDetachedFrom(this);

	}
}
