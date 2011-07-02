package org.bukkitcontrib.event.bukkitcontrib;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.block.ContribChunk;
import org.bukkitcontrib.block.ContribCraftChunk;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.util.ChunkUtil;

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
        
        //Start a task while already executing this task will cause it to be executed too soon,
        //hence the 5ms delay
        (new Thread() {
            public void run() {
                try {
                    sleep(5);
                } catch (InterruptedException e) {}
                Runnable update = new Runnable() {
                    public void run() {
                        BukkitContrib.playerListener.manager.onServerTick();
                        Player[] online = Bukkit.getServer().getOnlinePlayers();
                        for (Player player : online) {
                        	if (player instanceof ContribCraftPlayer) {
                        		((ContribCraftPlayer)player).onTick();
                        	}
                        }
                        ArrayList<ContribChunk> chunks = ChunkUtil.getAllLoadedChunks();
                        for (ContribChunk chunk : chunks) {
                        	((ContribCraftChunk)chunk).onTick();
                        }
                        Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent());
                    }
                };
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update, 0);
            }
        }).start();
        
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
