package org.getspout.unchecked.server.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.unchecked.server.SpoutServer;

public class ReloadCommand extends SpoutCommand {
	public ReloadCommand(SpoutServer server) {
		super(server, "reload", "Reloads the server or portions of the server", "all/*|aliases", "rl");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (args[0].matches("([Aa]ll|\\*)")) {
			if (!checkPermission(sender, "all")) {
				return false;
			}
			if (args.length > 3 && args[2].equalsIgnoreCase("yesiamthatdumb")) {
				tellOps(sender, "Reloading full server");
				server.reload();
			} else {
				sender.sendMessage(ChatColor.YELLOW + "To actually fully reload the server, use /reload all yesiamthatdumb");
				return false;
			}
		} else if (args[0].matches("[Aa]liases")) {
			if (!checkPermission(sender, "aliases")) {
				return false;
			}
			tellOps(sender, "Reloading command aliases");
			server.reloadCommandAliases();
		} else if (args[0].matches("config(uration)?")) {
			if (!checkPermission(sender, "config")) {
				return false;
			}
			tellOps(sender, "Reloading server configuration");
			server.reloadConfiguration();

		} else {
			sender.sendMessage(ChatColor.YELLOW + "Unknown option! Valid options are: " + getUsage());
			return false;
		}
		sender.sendMessage(ChatColor.GREEN + "Reload complete.");
		return true;
	}

	@Override
	public Set<Permission> registerPermissions(String prefix) {
		Set<Permission> ret = new HashSet<Permission>();
		ret.add(new Permission(prefix + ".all", "Gives permission for full server reloads"));
		ret.add(new Permission(prefix + ".aliases", "Gives permission to reload server command aliases"));
		ret.add(new Permission(prefix + ".config", "Gives permission to reload server configuration"));
		return ret;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
