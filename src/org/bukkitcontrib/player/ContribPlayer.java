package org.bukkitcontrib.player;

import java.lang.reflect.Field;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.BukkitContribNetServerHandler;

public class ContribPlayer extends CraftPlayer{

	public ContribPlayer(CraftServer server, EntityPlayer entity) {
		super(server, entity);
	}
	
    public static boolean updateNetServerHandler(Player player) {
        CraftPlayer cp = (CraftPlayer)player;
        CraftServer server = (CraftServer)BukkitContrib.getMinecraftServer();
        
        if (!(cp.getHandle().netServerHandler instanceof BukkitContribNetServerHandler)) {
            Location loc = player.getLocation();
            BukkitContribNetServerHandler handler = new BukkitContribNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
            handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            cp.getHandle().netServerHandler = handler;
            return true;
        }
        return false;
    }
    
    public static boolean updateBukkitEntity(Player player) {
    	if (!(player instanceof ContribPlayer)) {
    		CraftPlayer cp = (CraftPlayer)player;
    		EntityPlayer ep = cp.getHandle();
    		Field bukkitEntity;
			try {
				bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
				bukkitEntity.setAccessible(true);
	    		bukkitEntity.set(ep, new ContribPlayer((CraftServer)BukkitContrib.getMinecraftServer(), ep));
	    		return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
       	return false;
    }

}
