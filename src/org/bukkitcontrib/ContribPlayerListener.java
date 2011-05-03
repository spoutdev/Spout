package org.bukkitcontrib;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkitcontrib.player.ContribPlayer;

public class ContribPlayerListener extends PlayerListener{

    public void onPlayerJoin(PlayerJoinEvent event) {
        ContribPlayer.updateNetServerHandler(event.getPlayer());
        ContribPlayer.updateBukkitEntity(event.getPlayer());
    }
    
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
    	if (event.isCancelled()) {
    		return;
    	}
    	if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
    		Runnable update = new Runnable() {
    			public void run() {
    				ContribPlayer.updateBukkitEntity(event.getPlayer());
    			}
    		};
    		BukkitContrib.getMinecraftServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update);
    	}
    }
}
