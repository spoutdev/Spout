package org.bukkitcontrib;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkitcontrib.config.ConfigReader;

public class PlayerManager {
    private HashMap<String, Integer> timer = new HashMap<String, Integer>();
    
    public void onPlayerJoin(Player player){
        if (!ConfigReader.isForceClient()) {
            return;
        }
        timer.put(player.getName(), 200);
    }
    
    public void onServerTick() {
        if (!ConfigReader.isForceClient()) {
            return;
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (timer.containsKey(player.getName())) {
                int ticksLeft = timer.get(player.getName());
                if (--ticksLeft > 0) {
                    timer.put(player.getName(), ticksLeft);
                }
                else {
                    timer.remove(player.getName());
                    player.kickPlayer("This server requires the BukkitContrib SP mod! http://bit.ly/bukkitcontrib");
                }
            }
        }
    }
    
    public void onBukkitContribSPEnable(Player player) {
        if (!ConfigReader.isForceClient()) {
            return;
        }
        timer.remove(player.getName());
    }

}
