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

import java.util.Arrays;
import java.util.Collection;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.util.access.AccessManager;
import org.spout.api.util.access.BanType;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutServer;

public class ServerCommands extends CommonCommands{
	public ServerCommands(SpoutEngine engine) {
		super(engine);
	}

	@Override
	public SpoutServer getEngine() {
		return (SpoutServer) super.getEngine();
	}

	@Command(aliases = "whitelist", desc = "Add, remove, list, or toggle players on the whitelist.", usage = "<add|remove|list|on|off> [player] [reason]", min = 1, max = 3)
	@CommandPermissions("spout.command.whitelist")
	public void whitelist(CommandContext args, CommandSource source) {
		String arg1 = args.getString(0);
		AccessManager accessManager = getEngine().getAccessManager();
		if (args.length() == 1) {
			if (arg1.equalsIgnoreCase("list")) {
				Collection<String> c = accessManager.getWhitelistedPlayers();
				String[] whitelisted = c.toArray(new String[c.size()]);
				ChatArguments message = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Whitelisted (", whitelisted.length, "): ");
				for (int i = 0; i < whitelisted.length; i++) {
					message.append(ChatStyle.BLUE, whitelisted[i]);
					if (i != whitelisted.length - 1) {
						message.append(ChatStyle.RESET, ", ");
					}
				}
				source.sendMessage(message);
			}

			if (arg1.equalsIgnoreCase("on")) {
				SpoutConfiguration.WHITELIST_ENABLED.setValue(true);
				accessManager.setWhitelistEnabled(true);
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Toggled whitelist on.");
			}

			if (arg1.equalsIgnoreCase("off")) {
				SpoutConfiguration.WHITELIST_ENABLED.setValue(false);
				accessManager.setWhitelistEnabled(false);
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Toggled whitelist off.");
			}
		}

		if (args.length() == 2) {
			String arg2 = args.getString(1);
			if (arg1.equalsIgnoreCase("add")) {
				accessManager.whitelist(arg2);
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Added player '", arg2, "' to the whitelist.");
			}

			if (arg1.equalsIgnoreCase("remove")) {
				accessManager.unwhitelist(arg2);
				source.sendMessage(ChatStyle.BRIGHT_GREEN, "Removed player '", arg2, "' from the whitelist.");
			}
		}

		if (args.length() == 3) {
			if (arg1.equalsIgnoreCase("remove")) {
				accessManager.unwhitelist(args.getString(1), true, args.getJoinedString(2));
			}
		}
	}

	@Command(aliases = "banlist", usage = "[ips]", desc = "Shows banned players or ips.", min = 0, max = 1)
	@CommandPermissions("spout.command.banlist")
	public void banList(CommandContext args, CommandSource source) {
		BanType type;
		if (args.length() > 0 && args.getString(0).equalsIgnoreCase("ips")) {
			type = BanType.IP;
		} else {
			type = BanType.PLAYER;
		}

		AccessManager accessManager = getEngine().getAccessManager();
		Collection<String> c = accessManager.getBanned(type);
		String[] banned = c.toArray(new String[c.size()]);
		ChatArguments message = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Banned ", type == BanType.IP ? "IPs " : "", "(", banned.length, "): ");
		for (int i = 0; i < banned.length; i++) {
			message.append(ChatStyle.BLUE, banned[i]);
			if (i != banned.length - 1) {
				message.append(ChatStyle.RESET, ", ");
			}
		}
		source.sendMessage(message);
	}

	@Command(aliases = "ban", usage = "<player> [reason]", desc = "Ban a player", min = 1, max = -1)
	@CommandPermissions("spout.command.ban")
	public void ban(CommandContext args, CommandSource source) {
		String player = args.getString(0);
		if (args.length() < 2) {
			getEngine().getAccessManager().ban(BanType.PLAYER, player);
		} else {
			getEngine().getAccessManager().ban(BanType.PLAYER, player, true, args.getJoinedString(1));
		}
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Banned player '", player, "' from the server.");
	}

	@Command(aliases = "unban", usage = "<player>", desc = "Unban a player", min = 1, max = 1)
	@CommandPermissions("spout.command.unban")
	public void unban(CommandContext args, CommandSource source) {
		String player = args.getString(0);
		getEngine().getAccessManager().unban(BanType.PLAYER, player);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Unbanned player '", player, "' from the server.");
	}

	@Command(aliases = "ban-ip", usage = "<address> [reason]", desc = "Ban an IP address", min = 1, max = -1)
	@CommandPermissions("spout.command.banip")
	public void banIp(CommandContext args, CommandSource source) {
		if (source instanceof Player) {
			getEngine().getLogger().info(((Player) source).getAddress().getHostAddress());
			getEngine().getLogger().info("Args: " + args.length());
		}

		String address = args.getString(0);
		if (args.length() < 2) {
			getEngine().getAccessManager().ban(BanType.IP, address);
		} else {
			getEngine().getAccessManager().ban(BanType.IP, address, true, args.getJoinedString(1));
		}
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Banned IP address '", address, "' from the server.");
	}

	@Command(aliases = "unban-ip", usage = "<address>", desc = "Unban an IP address", min = 1, max = 1)
	@CommandPermissions("spout.command.unbanip")
	public void unbanIp(CommandContext args, CommandSource source) {
		String address = args.getString(0);
		getEngine().getAccessManager().unban(BanType.IP, address);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Unbanned IP address '", address, "' from the server");
	}

	@Command(aliases = "kick", usage = "<player> [message]", desc = "Kick a player", min = 1, max = -1)
	@CommandPermissions("spout.command.kick")
	public void kick(CommandContext args, CommandSource source) throws CommandException {
		String playerName = args.getString(0);
		ChatArguments message;
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		} else {
			message = new ChatArguments("You have been kicked from the server.");
		}

		Player player = getEngine().getPlayer(playerName, true);
		if (player == null) {
			throw new CommandException("Unknown player: " + player);
		}

		if (player.isOnline()) {
			player.kick(message);
			ChatArguments retMsg = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Kicked player '", player.getName(), "'");
			if (!message.getPlainString().isEmpty()) {
				retMsg.append(" for reason '").append(message).append("'");
			}
			source.sendMessage(retMsg);
		}
	}

	@Command(aliases = {"players", "who", "list"}, desc = "List all online players")
	@CommandPermissions("spout.command.players")
	public void list(CommandContext args, CommandSource source) {
		Player[] players = getEngine().getOnlinePlayers();
		ChatArguments onlineMsg = new ChatArguments(Arrays.asList("Online (", (players.length <= 0 ? ChatStyle.RED : ChatStyle.BRIGHT_GREEN), players.length, ChatStyle.RESET, "): "));
		for (int i = 0; i < players.length; i++) {
			if (!players[i].isOnline()) {
				continue;
			}
			onlineMsg.append(ChatStyle.BLUE).append(players[i].getName()).append(ChatStyle.RESET);
			if (i < players.length - 1) {
				onlineMsg.append(", ");
			}
		}
		source.sendMessage(onlineMsg);
	}

	@Command(aliases = "disconnect", desc = "Disconnect the client from the server", usage = "[message]", min = 0, max = -1)
	public void disconnectClient(CommandContext args, CommandSource source) {
		ChatArguments message;
		if (args.length() == 0) {
			message = new ChatArguments("Ciao!");
		} else {
			message = args.getJoinedString(0);
		}

		if (source instanceof Player) {
			((Player) source).getSession().disconnect(false, new Object[]{message});
		}
	}

	@Command(aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!", min = 1, max = -1)
	public void serverSay(CommandContext args, CommandSource source) throws CommandException {
		ChatArguments message = args.getJoinedString(0);
		if (!message.getPlainString().isEmpty()) {
			if (!source.hasPermission("spout.chat.send")) {
				throw new CommandException("You do not have permission to send chat messages");
			}

			ChatArguments template;
			String name;
			if (source instanceof Player) {
				Player player = (Player) source;
				PlayerChatEvent event = getEngine().getEventManager().callEvent(new PlayerChatEvent(player, message));
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
}
