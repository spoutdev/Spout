package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

public class SayCommand extends SpoutCommand {
	public SayCommand(SpoutServer server) {
		super(server, "say", "Talk", "<msg>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.GRAY + "You must actually type something to say something!");
			return false;
		}
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				build.append(" ");
			}
			build.append(args[i]);
		}
		String name = sender instanceof ConsoleCommandSender ? "Console" : sender.getName();
		server.broadcastMessage("<" + name + "> " + build.toString());
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
