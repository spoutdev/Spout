package org.getspout.server.command;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

/**
 * Command to ban players or IP addresses.
 */
public class GiveCommand extends SpoutCommand {

    private SpoutServer srv;
    private Material[] mats;

    public GiveCommand(SpoutServer server) {
        super(server, "give", "Give an op an item!", "[itemname] [amount]");
        srv = server;
        mats = Material.values();
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to give yourself items!");
            return true;
        }
        Player plr = (Player) sender;
        String itemname;
        Integer amount = 64;
        if (args.length == 2) {
            itemname = args[0];
        } else {
            itemname = args[1];
        }
        if (args.length != 1) {
            try {
                if (args.length == 2) {
                    amount = Integer.parseInt(args[1]);
                }
                if (args.length == 3) {
                    amount = Integer.parseInt(args[2]);
                }
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "Invalid number specified!");
                return false;
            }
        }
        Player giveplr = plr;
        if(args.length==3) {
            giveplr = srv.getPlayer(args[0]);
            if(giveplr==null) {
                plr.sendMessage(ChatColor.RED+"Could not find a player called "+args[0]+"! Please check your spelling!");
                return true;
            }
        }
        for (Material curmat : mats) {
            if (curmat.name().equalsIgnoreCase(itemname)) {
                ItemStack item = new ItemStack(curmat, amount);
                giveplr.getInventory().addItem(item);
                giveplr.sendMessage(ChatColor.GREEN + "You were given " + amount + " " + itemname + "!");
                if (!giveplr.getName().equals(plr.getName())) {
                    plr.sendMessage(ChatColor.GREEN + "Gave " + giveplr.getName() + " " + amount + " " + itemname + "!");
                }
                return true;
            }
        }
        plr.sendMessage(ChatColor.RED + "Could not find the material \"" + itemname + "\"!");
        return true;
    }

    @Override
    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> perms = new HashSet<Permission>();
        //perms.add(new Permission(prefix + ".give", "Allows player to give himself items!"));
        //not needed right now :P
        return perms;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
