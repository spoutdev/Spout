package org.getspout.server.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * Common base class for built-in Spout commands.
 */
public abstract class SpoutCommand extends Command {
	protected final SpoutServer server;
	public static final String PERM_PREFIX = "spout.command";

	public SpoutCommand(SpoutServer server, String name, String desc, String usage, String... aliases) {
		super(name, desc, "/" + name + " " + usage, Arrays.asList(aliases));
		this.server = server;
		setPermission(PERM_PREFIX + "." + name);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender)) {
			return false;
		}
		run(sender, commandLabel, args);
		return true;
	}

	protected boolean checkArgs(CommandSender sender, String[] args, int expected) {
		if (args.length != expected) {
			sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
			return false;
		}
		return true;
	}

	protected boolean checkArgs(CommandSender sender, String[] args, int min, int max) {
		if (args.length < min || args.length > max) {
			sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
			return false;
		}
		return true;
	}

	protected boolean tellOps(CommandSender sender, String message) {
		server.broadcast("(" + sender.getName() + ": " + message + ")", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
		return true;
	}

	public boolean checkPermission(CommandSender sender, String permission) {
		if (!sender.hasPermission(getPermission() + "." + permission)) {
			sender.sendMessage(ChatColor.RED + "I'm sorry Dave but I cannot let you do that.");
			return false;
		}
		return true;
	}

	public abstract boolean run(CommandSender sender, String commandLabel, String[] args);

	public Set<Permission> registerPermissions(String prefix) {
		return new HashSet<Permission>();
	}

	public abstract PermissionDefault getPermissionDefault();
}
