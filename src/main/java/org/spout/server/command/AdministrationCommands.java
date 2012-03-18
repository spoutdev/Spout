/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
package org.spout.server.command;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.ChatColor;
import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialData;
import org.spout.api.player.Player;
import org.spout.server.SpoutServer;

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
	public void stop(CommandContext args, CommandSource source) throws CommandException {
		String message = "Server shutting down";
		if (args.length() > 0) {
			message = args.getJoinedString(0);
		}
		server.stop(message);
	}

	@Command(desc = "Writes the stack trace of all active threads to the logs", max = -1, aliases = {""})
	@CommandPermissions("spout.command.dumpstack")
	public void dumpstack(CommandContext args, CommandSource source) throws CommandException {
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
	public void kick(CommandContext args, CommandSource source) throws CommandException {
		String playerName = args.getString(0);
		String message = "You have been kicked from the server.";
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		}

		Player player = Spout.getGame().getPlayer(playerName, true);
		if (player != null) {
			player.kick(message);
			source.sendMessage(ChatColor.BRIGHT_GREEN + "Kicked player '" + player.getName() + (!message.isEmpty() ? "' for reason '" + message + "'" : "'"));
		}
	}

	@Command(aliases = "give", usage = "<player> <id> [count] [damage]", desc = "Give a player an item", min = 2, max = 4)
	@CommandPermissions("spout.command.give")
	public void give(CommandContext args, CommandSource source) throws CommandException {
		String playerName = args.getString(0);

		Player player = Spout.getGame().getPlayer(playerName, true);
		if (player == null) {
			throw new CommandException(playerName + " is not online.");
		}
		
		Material mat = null;
		if (args.isInteger(1)) {
			short id = (short)args.getInteger(1);
			mat = MaterialData.getMaterial(id);
		} else {
			mat = MaterialData.getMaterial(args.getString(1));
		}
		if (mat == null) {
			throw new CommandException(args.getString(1) + " is not a valid material.");
		}
		
		int count = mat.getMaxStackSize();
		if (args.length() >= 3 && args.isInteger(2)) {
			count = args.getInteger(2);
		}
		
		short damage = 0;
		if (args.length() >= 4 && args.isInteger(3)) {
			damage = (short)args.getInteger(3);
		}
		
		ItemStack stack = new ItemStack(mat, count, damage);
		
		Inventory inv = player.getEntity().getInventory();
		if (inv.addItem(stack) == false) {
			throw new CommandException("The inventory of " + playerName + " is full.");
		}
		
		source.sendMessage("Giving " + playerName + " " + count + " " + mat.getName() + ".");
	}
}
