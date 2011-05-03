package org.bukkitcontrib;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitcontrib.event.inventory.InventoryListener;
import org.bukkitcontrib.player.ContribCraftPlayer;


public class BukkitContrib extends JavaPlugin{
    private static final ContribPlayerListener playerListener = new ContribPlayerListener();
    private static final InventoryListener inventoryListener = new InventoryListener();
    private static BukkitContrib instance;
    @Override
    public void onDisable() {
        Player[] online = getServer().getOnlinePlayers();
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
        }
    }

    @Override
    public void onEnable() {
        BukkitContrib.instance = this;
        getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, inventoryListener, Priority.Normal, this);

        Player[] online = getServer().getOnlinePlayers();
        for (Player player : online) {
            ContribCraftPlayer.removeBukkitEntity(player);
            ContribCraftPlayer.updateBukkitEntity(player);
        }
        
        Logger.getLogger("Minecraft").info("BukkitContrib " + this.getDescription().getVersion() + " has been initialized");
    }

    public static BukkitContrib getInstance() {
        return instance;
    }

}
