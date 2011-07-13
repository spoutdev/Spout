package org.bukkitcontrib.event.bukkitcontrib;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.block.ContribCraftChunk;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class ServerTickEvent extends Event{
    protected static long lastTickTime = System.currentTimeMillis();
    protected long lastTick;
    protected long createdTime = System.currentTimeMillis();
    protected static boolean first = true;
    public ServerTickEvent() {
        super("ServerTickEvent");
        if (!first) {
            lastTick = lastTickTime;
        }
        else {
            lastTick = createdTime - 1000;
            first = false;
        }
        lastTickTime = createdTime;

        Runnable update = new Runnable() {
            public void run() {
                BukkitContrib.playerListener.manager.onServerTick();
                Player[] online = Bukkit.getServer().getOnlinePlayers();
                for (Player player : online) {
                    if (player instanceof ContribCraftPlayer) {
                        ((ContribCraftPlayer)player).onTick();
                    }
                }
                for (World w : Bukkit.getServer().getWorlds()) {
                    Chunk[] chunks = w.getLoadedChunks();
                    for (Chunk chunk : chunks) {
                        if (chunk instanceof ContribCraftChunk) {
                            ((ContribCraftChunk)chunk).onTick();
                        }
                    }
                }
                Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent());
            }
        };
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BukkitContrib.getInstance(), update, 0, 1);
        
    }
    
    /**
     * Returns the milliseconds since the last server tick event was created
     * Ideally, it should be exactly 50 milliseconds, but because of server lag, it may be more
     * @return milliseconds since last server tick
     */
    public Long getMillisLastTick() {
        return Math.abs(createdTime - lastTick);
    }
    
    /**
     * Returns the seconds since the last server tick event was created
     * Ideally
     * @return
     */
    public double getSecondsLastTick() {
        return ((double)Math.abs(createdTime - lastTick)) / 1000;
    }

}
