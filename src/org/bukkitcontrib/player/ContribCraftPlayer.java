package org.bukkitcontrib.player;

import java.lang.reflect.Field;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;

import org.bukitcontrib.inventory.ContribCraftInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.ContribNetServerHandler;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;

public class ContribCraftPlayer extends CraftPlayer implements ContribPlayer{

    public ContribCraftPlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);
    }
    
    public boolean closeActiveWindow() {
        InventoryCloseEvent event = new InventoryCloseEvent(this, getActiveInventory());
        BukkitContrib.getMinecraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        getHandle().x();
        return true;
    }
    
    public boolean openInventoryWindow(Inventory inventory) {
        InventoryOpenEvent event = new InventoryOpenEvent(this, inventory);
        BukkitContrib.getMinecraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        IInventory dialog = ((CraftInventory)event.getInventory()).getInventory();
        if (dialog instanceof TileEntityDispenser) {
            getHandle().a((TileEntityDispenser)dialog);
        }
        else if (dialog instanceof TileEntityFurnace) {
            getHandle().a((TileEntityFurnace)dialog);
        }
        else {
            getHandle().a(dialog);
        }
        return true;
    }
    
    public boolean openWorkbenchWindow(Location location) {
        if (location.getBlock().getType() != Material.WORKBENCH) {
            throw new UnsupportedOperationException("Must be a valid workbench!");
        }
        else {
            ContainerWorkbench temp = new ContainerWorkbench(getHandle().inventory, ((CraftWorld)location.getWorld()).getHandle(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            IInventory inventory = temp.b;
            InventoryOpenEvent event = new InventoryOpenEvent(this, new ContribCraftInventory(inventory));
            BukkitContrib.getMinecraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            getHandle().a(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            return true;
        }
    }
    
    public Inventory getActiveInventory() {
        return getNetServerHandler().getCraftInventory(getNetServerHandler().getActiveInventory());
    }
    
    public ContribNetServerHandler getNetServerHandler() {
        return (ContribNetServerHandler) getHandle().netServerHandler;
    }
    
    public static boolean updateNetServerHandler(Player player) {
        CraftPlayer cp = (CraftPlayer)player;
        CraftServer server = (CraftServer)BukkitContrib.getMinecraftServer();
        
        if (!(cp.getHandle().netServerHandler instanceof ContribNetServerHandler)) {
            Location loc = player.getLocation();
            ContribNetServerHandler handler = new ContribNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
            handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            cp.getHandle().netServerHandler = handler;
            return true;
        }
        return false;
    }
    
    public static boolean updateBukkitEntity(Player player) {
        if (!(player instanceof ContribCraftPlayer)) {
            CraftPlayer cp = (CraftPlayer)player;
            EntityPlayer ep = cp.getHandle();
            Field bukkitEntity;
            try {
                bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
                bukkitEntity.setAccessible(true);
                bukkitEntity.set(ep, new ContribCraftPlayer((CraftServer)BukkitContrib.getMinecraftServer(), ep));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
           return false;
    }
    
    public static ContribPlayer getContribPlayer(Player player) {
        if (player instanceof ContribCraftPlayer) {
            return (ContribCraftPlayer)player;
        }
        updateBukkitEntity(player);
        return (ContribCraftPlayer)((CraftPlayer)player).getHandle().getBukkitEntity();
    }

}
