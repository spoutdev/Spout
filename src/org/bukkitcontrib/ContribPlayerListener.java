package org.bukkitcontrib;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class ContribPlayerListener extends PlayerListener{

    public void onPlayerJoin(PlayerJoinEvent event) {
        ContribCraftPlayer.updateNetServerHandler(event.getPlayer());
        ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
    }
    
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
            Runnable update = new Runnable() {
                public void run() {
                    ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
                }
            };
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update);
        }
    }
}
