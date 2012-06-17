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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.ChatStyle;
import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.event.player.PlayerPreLoginEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.player.Player;
import org.spout.api.plugin.Plugin;

import org.spout.engine.SpoutServer;

/**
 * Commands related to server administration
 */
public class AdministrationCommands {
	private final SpoutServer server;

	public AdministrationCommands(SpoutServer server) {
		this.server = server;
	}

	@Command(aliases = "stop", usage = "[message]", desc = "Stop the server!", max = -1)
	@CommandPermissions("spout.command.stop")
	public void stop(CommandContext args, CommandSource source) {
		String message = "Server shutting down";
		if (args.length() > 0) {
			message = args.getJoinedString(0);
		}
		server.stop(message);
	}

	@Command(desc = "Writes the stack trace of all active threads to the logs", max = -1, aliases = {""})
	@CommandPermissions("spout.command.dumpstack")
	public void dumpstack(CommandContext args, CommandSource source) {
		Map<Thread, StackTraceElement[]> dump = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> i = dump.entrySet().iterator();
		server.getLogger().info("[--------------Thread Stack Dump--------------]");
		while (i.hasNext()) {
			Entry<Thread, StackTraceElement[]> e = i.next();
			server.getLogger().info("Thread: " + e.getKey().getName());
			for (StackTraceElement element : e.getValue()) {
				server.getLogger().info("    " + element.toString());
			}
			server.getLogger().info("");
		}
		server.getLogger().info("[---------------End Stack Dump---------------]");
	}

	@Command(aliases = "kick", usage = "<player> [message]", desc = "Kick a player", min = 1, max = -1)
	@CommandPermissions("spout.command.kick")
	public void kick(CommandContext args, CommandSource source) {
		String playerName = args.getString(0);
		String message = "You have been kicked from the server.";
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		}

		Player player = Spout.getEngine().getPlayer(playerName, true);
		if (player.isOnline()) {
			player.kick(message);
			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Kicked player '" + player.getName() + (!message.isEmpty() ? "' for reason '" + message + "'" : "'"));
		}
	}

	@Command(aliases = "reload", usage = "[plugin]", desc = "Reload server and/or plugins", max = 1)
	@CommandPermissions("spout.command.reload")
	public void reload(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 0) {
			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Reloading server...");

			for (Plugin plugin : Spout.getEngine().getPluginManager().getPlugins()) {
				if (plugin.getDescription().allowsReload()) {
					plugin.onReload();
				}
			}

			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Reloaded.");
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
			source.sendMessage(ChatStyle.BRIGHT_GREEN + "Reloaded '" + pluginName + "'.");
		}
	}
	@Command(aliases = {"plugins", "pl"}, desc = "List all plugins on the server")
	@CommandPermissions("spout.command.plugins")
	public void plugins(CommandContext args, CommandSource source) {
		Plugin[] pluginList = Spout.getEngine().getPluginManager().getPlugins();
		String pluginListString = "Plugins (" + pluginList.length + "): ";

		for (int i = 0; i < pluginList.length; i++) {
			if (pluginList[i].getName().equalsIgnoreCase("Spout")) {
				continue;
			}

			if (pluginList[i].isEnabled()) {
				pluginListString += ChatColor.BRIGHT_GREEN + pluginList[i].getName();
			} else {
				pluginListString += ChatColor.RED + pluginList[i].getName();
			}

			if (i != pluginList.length - 1) {
				pluginListString += ChatColor.WHITE + ", ";
			}
		}
		source.sendMessage(pluginListString);
	}

	@Command(aliases = {"players", "who"}, desc = "List all online players")
	@CommandPermissions("spout.command.players")
	public void onPlayersCommand(CommandContext args, CommandSource source) {
		Player[] players = Spout.getEngine().getOnlinePlayers();
		String onlineMsg = "Online (" + (players.length <= 0 ? ChatColor.RED : ChatColor.BRIGHT_GREEN) + players.length + ChatColor.WHITE + "): ";
		StringBuilder playerListMsg = new StringBuilder();
		for (int i = 0; i < players.length; i ++) {
			if (!players[i].isOnline()) {
				continue;
			}
			playerListMsg.append(ChatColor.BLUE + players[i].getName() + ChatColor.WHITE);
			if (i < players.length - 1) {
				playerListMsg.append(", ");
			}
		}
		source.sendMessage(onlineMsg + playerListMsg.toString());
	}
}
