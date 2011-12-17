package org.getspout.server.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * Command to ban players or IP addresses.
 */
public class TpCommand extends SpoutCommand {
    private SpoutServer srv;
	public TpCommand(SpoutServer server) {
		super(server, "tp", "Teleport to a player", " [playername] [secondplayer]");
        this.srv=server;
	}
	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if(args.length==0) {
            sender.sendMessage("You need to specify a player name!");
            return true;
        }
		if(!(sender instanceof Player)&&args.length==1){
            sender.sendMessage("You need to be a player to do that!");
            return true;
        }
        if(args.length==1) {
            Player plr = (Player) sender;
            Player other = srv.getPlayer(args[0]);
            if(other==null) {
                plr.sendMessage("Could not find a player called "+args[0]+"! Check your spelling!");
                return true;
            }
            plr.teleport(other);
            plr.sendMessage("Teleported you to "+other.getName()+"!");
            return true;
        }
        Player plr1 = srv.getPlayer(args[0]);
        Player plr2 = srv.getPlayer(args[1]);
        if(plr1==null||plr2==null) {
            sender.sendMessage("One of the players is not online!");
            return true;
        }
        plr1.teleport(plr2);
        sender.sendMessage("Teleported "+plr1.getName()+" to "+plr2.getName()+"!");
        return true;
	}

	@Override
	public Set<Permission> registerPermissions(String prefix) {
		Set<Permission> perms = new HashSet<Permission>();
		return perms;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
