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
package org.spout.engine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.Plugin;

import org.spout.engine.SpoutEngine;

public class AdministrationCommands {
	private final SpoutEngine engine;

	public AdministrationCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = "stop", usage = "[message]", desc = "Stop the server!", max = -1)
	@CommandPermissions("spout.command.stop")
	public void stop(CommandContext args, CommandSource source) {
		String message = "Engine halting";
		switch (Spout.getPlatform()) {
			case CLIENT:
				message = "Client halting";
				break;
			case PROXY:
				message = "Proxy halting";
				break;
			case SERVER:
				message = "Server halting";
				break;
		}
		if (args.length() > 0) {
			message = args.getJoinedString(0).getPlainString();
		}
		engine.stop(message);
	}

	@Command(desc = "Writes the stack trace of all active threads to the logs", max = -1, aliases = {""})
	@CommandPermissions("spout.command.dumpstack")
	public void dumpstack(CommandContext args, CommandSource source) {
		Map<Thread, StackTraceElement[]> dump = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> i = dump.entrySet().iterator();
		engine.getLogger().info("[--------------Thread Stack Dump--------------]");
		while (i.hasNext()) {
			Entry<Thread, StackTraceElement[]> e = i.next();
			engine.getLogger().info("Thread: " + e.getKey().getName());
			for (StackTraceElement element : e.getValue()) {
				engine.getLogger().info("    " + element.toString());
			}
			engine.getLogger().info("");
		}
		engine.getLogger().info("[---------------End Stack Dump---------------]");
	}

	@Command(aliases = "kick", usage = "<player> [message]", desc = "Kick a player", min = 1, max = -1)
	@CommandPermissions("spout.command.kick")
	public void kick(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			source.sendMessage(ChatStyle.RED, "Kick is available only in server-mode.");
			return;
		}
		String playerName = args.getString(0);
		ChatArguments message;
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		} else {
			message = new ChatArguments("You have been kicked from the server.");
		}

		Player player = ((Server) Spout.getEngine()).getPlayer(playerName, true);
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

	@Command(aliases = "ban", usage = "<player> [reason]", desc = "Ban a player", min = 1, max = -1)
	@CommandPermissions("spout.command.ban")
	public void ban(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new CommandException("Banning is only available in server mode.");
		}

		Server server = (Server) Spout.getEngine();
		String player = args.getString(0);
		if (args.length() < 2) {
			server.banPlayer(player);
		} else {
			server.banPlayer(player, true, args.getJoinedString(1));
		}
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Banned player '", player, "' from the server.");
	}

	@Command(aliases = "unban", usage = "<player>", desc = "Unban a player", min = 1, max = 1)
	@CommandPermissions("spout.command.unban")
	public void unban(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new CommandException("Unbanning is only available in server mode.");
		}
		String player = args.getString(0);
		((Server) Spout.getEngine()).unbanPlayer(player);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Unbanned player '", player, "' from the server.");
	}

	@Command(aliases = "banip", usage = "<address> [reason]", desc = "Ban an IP address", min = 1, max = -1)
	@CommandPermissions("spout.command.banip")
	public void banIp(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new CommandException("Banning is only available in server mode.");
		}

		if (source instanceof Player) {
			System.out.println(((Player) source).getAddress().getHostAddress());
			System.out.println("Args: " + args.length());
		}

		Server server = (Server) Spout.getEngine();
		String address = args.getString(0);
		if (args.length() < 2) {
			server.banIp(address);
		} else {
			server.banIp(address, true, args.getJoinedString(1));
		}
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Banned IP address '", address, "' from the server.");
	}

	@Command(aliases = "unbanip", usage = "<address>", desc = "Unban an IP address", min = 1, max = 1)
	@CommandPermissions("spout.command.unbanip")
	public void unbanIp(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new CommandException("Unbanning is only available in server mode.");
		}
		String address = args.getString(0);
		((Server) Spout.getEngine()).unbanIp(address);
		source.sendMessage(ChatStyle.BRIGHT_GREEN, "Unbanned IP address '", address, "' from the server");
	}

	@Command(aliases = "reload", usage = "[plugin]", desc = "Reload engine and/or plugins", max = 1)
	@CommandPermissions("spout.command.reload")
	public void reload(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 0) {
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Reloading engine...");

			for (Plugin plugin : Spout.getEngine().getPluginManager().getPlugins()) {
				if (plugin.getDescription().allowsReload()) {
					plugin.onReload();
				}
			}

			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Reloaded.");
		} else {
			String pluginName = args.getString(0);
			if (Spout.getEngine().getPluginManager().getPlugin(pluginName) == null) {
				throw new CommandException("'" + pluginName + "' is not a valid plugin name.");
			}

			Plugin plugin = Spout.getEngine().getPluginManager().getPlugin(pluginName);
			if (!plugin.getDescription().allowsReload()) {
				throw new CommandException("The plugin '" + pluginName + "' does not allow reloads.");
			}
			plugin.onReload();
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Reloaded '", pluginName, "'.");
		}
	}

	@Command(aliases = {"plugins", "pl"}, desc = "List all plugins on the engine")
	@CommandPermissions("spout.command.plugins")
	public void plugins(CommandContext args, CommandSource source) {
		Plugin[] pluginList = Spout.getEngine().getPluginManager().getPlugins();
		ChatArguments pluginListString = new ChatArguments();
		pluginListString.append(Arrays.<Object>asList("Plugins (", pluginList.length - 1, "): "));

		for (int i = 0; i < pluginList.length; i++) {
			if (pluginList[i] instanceof SpoutMetaPlugin) {
				continue;
			}

			pluginListString.append(pluginList[i].isEnabled() ? ChatStyle.BRIGHT_GREEN : ChatStyle.RED)
					.append(pluginList[i].getName());

			if (i != pluginList.length - 1) {
				pluginListString.append(ChatStyle.RESET).append(", ");
			}
		}
		source.sendMessage(pluginListString);
	}

	@Command(aliases = {"players", "who", "list"}, desc = "List all online players")
	@CommandPermissions("spout.command.players")
	public void list(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER || Spout.getPlatform() != Platform.PROXY) {
			throw new CommandException("You may only list online players in server mode.");
		}

		Player[] players = ((Server) Spout.getEngine()).getOnlinePlayers();
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
}
