/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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

import java.util.Collection;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.command.annotated.Flag;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.util.access.AccessManager;
import org.spout.api.util.access.BanType;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutServer;

public class ServerCommands extends CommonCommands {
	public ServerCommands(SpoutEngine engine) {
		super(engine);
	}

	@Override
	public SpoutServer getEngine() {
		return (SpoutServer) super.getEngine();
	}

	@CommandDescription (aliases = "worlds", desc = "Lists the worlds currently loaded")
	@Permissible ("spout.command.worlds")
	public void worlds(CommandSource source, CommandArguments args) throws CommandException {
		args.assertCompletelyParsed();

		source.sendMessage("Worlds:");
		for (World w : getEngine().getWorlds()) {
			source.sendMessage("    world: " + w.getName());
		}
	}

	@CommandDescription (aliases = "whitelist", desc = "Add, remove, list, or toggle players on the whitelist.", usage = "<add|remove|list|on|off> [player] [reason]")
	@Permissible ("spout.command.whitelist")
	public void whitelist(CommandSource source, CommandArguments args) throws CommandException {
		String action = args.popString("action");
		AccessManager accessManager = getEngine().getAccessManager();

		if (action.equalsIgnoreCase("list")) {
			Collection<String> c = accessManager.getWhitelistedPlayers();
			String[] whitelisted = c.toArray(new String[c.size()]);
			StringBuilder message = new StringBuilder("Whitelisted (" + whitelisted.length + "): ");
			for (int i = 0; i < whitelisted.length; i++) {
				message.append(whitelisted[i]);
				if (i != whitelisted.length - 1) {
					message.append(", ");
				}
			}
			source.sendMessage(message.toString());
		} else if (action.equalsIgnoreCase("on")) {
			SpoutConfiguration.WHITELIST_ENABLED.setValue(true);
			accessManager.setWhitelistEnabled(true);
			source.sendMessage("Toggled whitelist on.");
		} else if (action.equalsIgnoreCase("off")) {
			SpoutConfiguration.WHITELIST_ENABLED.setValue(false);
			accessManager.setWhitelistEnabled(false);
			source.sendMessage("Toggled whitelist off.");
		} else {
			String target = args.popString("target");
			if (action.equalsIgnoreCase("add")) {
				accessManager.whitelist(target);
				source.sendMessage("Added player '" + target + "' to the whitelist.");
			}

			if (action.equalsIgnoreCase("remove")) {
				accessManager.unwhitelist(target);
				source.sendMessage("Removed player '" + target + "' from the whitelist.");
			}

			if (action.equalsIgnoreCase("remove")) {
				accessManager.unwhitelist(target, true, args.popRemainingStrings("reason"));
			}
		}
	}

	@CommandDescription (aliases = "banlist", usage = "[--ips]", flags = {@Flag (aliases = {"ips", "i"})},
			desc = "Shows banned players or ips.")
	@Permissible ("spout.command.banlist")
	public void banList(CommandSource source, CommandArguments args) throws CommandException {
		BanType type = args.has("ips") ? BanType.IP : BanType.PLAYER;
		AccessManager accessManager = getEngine().getAccessManager();
		Collection<String> c = accessManager.getBanned(type);
		String[] banned = c.toArray(new String[c.size()]);

		StringBuilder message = new StringBuilder("Banned " + (type == BanType.IP ? "IPs " : "" + "(" + banned.length + "): "));
		for (int i = 0; i < banned.length; i++) {
			message.append(banned[i]);
			if (i != banned.length - 1) {
				message.append(", ");
			}
		}
		source.sendMessage(message.toString());
	}

	@CommandDescription (aliases = "ban", usage = "<player> [reason]", desc = "Ban a player")
	@Permissible ("spout.command.ban")
	public void ban(CommandSource source, CommandArguments args) throws CommandException {
		String player = args.popString("player");
		if (args.hasMore()) {
			getEngine().getAccessManager().ban(BanType.PLAYER, player, true, args.popRemainingStrings("reason"));
		} else {
			getEngine().getAccessManager().ban(BanType.PLAYER, player);
		}
		source.sendMessage("Banned player '" + player + "' from the server.");
	}

	@CommandDescription (aliases = "unban", usage = "<player>", desc = "Unban a player")
	@Permissible ("spout.command.unban")
	public void unban(CommandSource source, CommandArguments args) throws CommandException {
		String player = args.popString("player");
		args.assertCompletelyParsed();

		getEngine().getAccessManager().unban(BanType.PLAYER, player);
		source.sendMessage("Unbanned player '" + player + "' from the server.");
	}

	@CommandDescription (aliases = "ban-ip", usage = "<address> [reason]", desc = "Ban an IP address")
	@Permissible ("spout.command.banip")
	public void banIp(CommandSource source, CommandArguments args) throws CommandException {
		if (source instanceof Player) {
			getEngine().getLogger().info(((Player) source).getAddress().getHostAddress());
			getEngine().getLogger().info("Args: " + args.length());
		}

		String address = args.popString("address");
		if (args.hasMore()) {
			getEngine().getAccessManager().ban(BanType.IP, address, true, args.popRemainingStrings("reason"));
		} else {
			getEngine().getAccessManager().ban(BanType.IP, address);
		}
		source.sendMessage("Banned IP address '" + address + "' from the server.");
	}

	@CommandDescription (aliases = "unban-ip", usage = "<address>", desc = "Unban an IP address")
	@Permissible ("spout.command.unbanip")
	public void unbanIp(CommandSource source, CommandArguments args) throws CommandException {
		String address = args.popString("address");
		args.assertCompletelyParsed();

		getEngine().getAccessManager().unban(BanType.IP, address);
		source.sendMessage("Unbanned IP address '" + address + "' from the server");
	}

	@CommandDescription (aliases = "kick", usage = "<player> [message]", desc = "Kick a player")
	@Permissible ("spout.command.kick")
	public void kick(CommandSource source, CommandArguments args) throws CommandException {
		Player player = args.popPlayer("player");
		String message = args.popRemainingStrings("message", "You have been kicked from the server.");

		if (player.isOnline()) {
			player.kick(message);
			StringBuilder retMsg = new StringBuilder("Kicked player '" + player.getName() + "'");
			if (!message.isEmpty()) {
				retMsg.append(" for reason '").append(message).append("'");
			}
			source.sendMessage(retMsg.toString());
		}
	}

	@CommandDescription (aliases = {"players", "who", "list"}, desc = "List all online players")
	@Permissible ("spout.command.players")
	public void list(CommandSource source, CommandArguments args) throws CommandException {
		args.assertCompletelyParsed();

		Player[] players = getEngine().getOnlinePlayers();
		StringBuilder onlineMsg = new StringBuilder("Online (" + players.length + "): ");
		for (int i = 0; i < players.length; i++) {
			if (!players[i].isOnline()) {
				continue;
			}
			onlineMsg.append(players[i].getName());
			if (i < players.length - 1) {
				onlineMsg.append(", ");
			}
		}
		source.sendMessage(onlineMsg.toString());
	}

	@CommandDescription (aliases = "disconnect", desc = "Disconnect the client from the server", usage = "[message]")
	public void disconnectClient(CommandSource source, CommandArguments args) throws CommandException {
		String message = args.popRemainingStrings("message", "Oops!");

		if (source instanceof Player) {
			((Player) source).getSession().disconnect(false, message);
		}
	}

	@CommandDescription (aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!")
	public void serverSay(CommandSource source, CommandArguments args) throws CommandException {
		String message = args.popRemainingStrings("message", source.getName() + " has nothing to say!");

		if (!source.hasPermission("spout.chat.send")) {
			throw new CommandException("You do not have permission to send chat messages");
		}

		String name;
		if (source instanceof Player) {
			Player player = (Player) source;
			PlayerChatEvent event = getEngine().getEventManager().callEvent(new PlayerChatEvent(player, message));
			if (event.isCancelled()) {
				return;
			}
			name = player.getDisplayName();
			message = event.getMessage();
		} else {
			name = source.getName();
		}
		getEngine().broadcastMessage("<" + name + "> " + message);
	}
}
