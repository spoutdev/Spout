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
 * Command to give items to players.
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
        if(args.length==0) {
            sender.sendMessage("You need to specify a player name!");
            return true;
        }
        String itemname;
        Integer amount = 64;
        if (args.length <3) {
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
        Player giveplr = null;
        if (sender instanceof Player) {
            giveplr = (Player) sender;
        }
        if (args.length == 3) {
            giveplr = srv.getPlayer(args[0]);
            if (giveplr == null) {
                sender.sendMessage(ChatColor.RED + "Could not find a player called " + args[0] + "! Please check your spelling!");
                return true;
            }
        }
        if (giveplr == null) {
            sender.sendMessage("You can't give yourself items, you're the console! Please use /give [name] [itemName] [amount]!");
            return true;
        }

        for (Material curmat : mats) {
            if (curmat.name().equalsIgnoreCase(itemname)) {
                ItemStack item = new ItemStack(curmat, amount);
                giveplr.getInventory().addItem(item);
                giveplr.sendMessage(ChatColor.GREEN + "You were given " + amount + " " + itemname + "!");
                if (!(sender instanceof Player)||(sender instanceof Player && !((Player) sender).getName().equals(giveplr.getName()))) {//if the 2 players are different
                    sender.sendMessage(ChatColor.GREEN + "Gave " + giveplr.getName() + " " + amount + " " + itemname + "!");
                }
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Could not find the material \"" + itemname + "\"!");
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
