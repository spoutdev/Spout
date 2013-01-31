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
package org.spout.engine.command;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.channel.ChatChannel;
import org.spout.api.chat.conversation.Conversation;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.command.annotated.Executor;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.plugin.Platform;

import org.spout.engine.SpoutEngine;

/**
 * Commands relating to messaging
 */
public class MessagingCommands {
	private final SpoutEngine engine;

	public MessagingCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!", min = 1, max = -1)
	public class SayCommand {
		public SayCommand() {
			engine.getDefaultPermissions().addDefaultPermission("spout.chat.send");
			engine.getDefaultPermissions().addDefaultPermission("spout.chat.receive.*");
		}

		@Executor(Platform.SERVER)
		public void server(CommandContext args, CommandSource source) throws CommandException {
			ChatArguments message = args.getJoinedString(0);
			if (!message.getPlainString().isEmpty()) {
				if (!source.hasPermission("spout.chat.send")) {
					throw new CommandException("You do not have permission to send chat messages");
				}

				ChatArguments template;
				String name;
				if (source instanceof Player) {
					Player player = (Player) source;
					PlayerChatEvent event = Spout.getEngine().getEventManager().callEvent(new PlayerChatEvent(player, message));
					if (event.isCancelled()) {
						return;
					}
					name = player.getDisplayName();
					template = event.getFormat().getArguments();
					message = event.getMessage();
				} else {
					name = source.getName();
					template = new ChatArguments("<", PlayerChatEvent.NAME, "> ", PlayerChatEvent.MESSAGE);
				}

				template.setPlaceHolder(PlayerChatEvent.NAME, new ChatArguments(name));
				template.setPlaceHolder(PlayerChatEvent.MESSAGE, message);

				source.getActiveChannel().broadcastToReceivers(source, template);
			}
		}

		@Executor(Platform.CLIENT)
		public void client(CommandContext args, CommandSource source) {
			engine.getCommandSource().sendMessage(args.getJoinedString(0));
		}
	}

	@Command(aliases = {"tell", "msg"}, usage = "<target> <message>", desc = "Tell a message to a specific user", min = 2)
	@CommandPermissions("spout.command.tell")
	public void tell(CommandContext args, CommandSource source) throws CommandException {
		String playerName = args.getString(0);
		ChatArguments message = args.getJoinedString(1);
		Player player = engine.getPlayer(playerName, false);
		if (player == source) {
			source.sendMessage("Forever alone.");
		} else if (player != null) {
			source.sendMessage("To ", ChatStyle.BRIGHT_GREEN, player.getName(), ChatStyle.RESET, ": ", message);
			player.sendMessage("From ", ChatStyle.BRIGHT_GREEN, source.getName(), ChatStyle.RESET, ": ", message);
		} else {
			throw new CommandException("Player '" + playerName + "' not found.");
		}
	}

	@Command(aliases = {"emote", "me", "action"}, usage = "<action>", desc = "Emote in the third person", min = 1)
	@CommandPermissions("spout.command.emote")
	public void emote(CommandContext args, CommandSource source) throws CommandException {
		source.getActiveChannel().broadcastToReceivers(new ChatArguments(ChatStyle.YELLOW, ChatStyle.ITALIC, source.getName(), " ", args.getJoinedString(0)));
	}

	@Command(aliases = {"endconvo"}, desc = "Removes the conversation you are currently in", min = 0, max = 0)
	@CommandPermissions("spout.command.endconversation")
	public void endconvo(CommandContext args, CommandSource source) throws CommandException {
		ChatChannel current = source.getActiveChannel();
		if (!(current instanceof Conversation)) {
			throw new CommandException("You are not currently in a conversation!");
		}

		if (((Conversation) current).finish()) {
			source.sendMessage(ChatStyle.CYAN, "Conversation exited!");
		} else {
			throw new CommandException("Unable to exit conversation! There's some sort of state issue!");
		}
	}
}
