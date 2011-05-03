package org.bukkitcontrib;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitcontrib.event.inventory.InventoryListener;


public class BukkitContrib extends JavaPlugin{
    private static final ContribPlayerListener playerListener = new ContribPlayerListener();
    private static final InventoryListener inventoryListener = new InventoryListener();
    private static Server server;
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        BukkitContrib.server = getServer();
        getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, inventoryListener, Priority.Normal, this);
        Logger.getLogger("Minecraft").info("BukkitContrib " + this.getDescription().getVersion() + " has been initialized");
    }
    
    public static Server getMinecraftServer() {
        return server;
    }

}
