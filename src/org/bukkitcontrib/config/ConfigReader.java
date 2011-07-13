package org.bukkitcontrib.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.util.config.Configuration;
import org.bukkitcontrib.BukkitContrib;

public class ConfigReader {
    private static boolean forceClient = false;
    private static boolean autoUpdate = false;
    private static String kickMessage = "This server requires the BukkitContrib SP mod! http://bit.ly/bukkitcontrib";
    
    public void read() {
        try {
            File directory = BukkitContrib.getInstance().getDataFolder();
            if (!directory.exists()) {
                directory.mkdir();
            }
            File config = new File(directory, "config.yml");
            if (!config.exists()) {
                config.createNewFile();
            }
            Configuration configuration = BukkitContrib.getInstance().getConfiguration();
            configuration.load();
            
            if (configuration.getProperty("ForceSinglePlayerClient") != null) {
                forceClient = configuration.getBoolean("ForceSinglePlayerClient", false);
            }
            else {
                configuration.setProperty("ForceSinglePlayerClient", false);
            }
            
            if (configuration.getProperty("ForceSinglePlayerClientKickMessage") != null) {
                kickMessage = configuration.getString("ForceSinglePlayerClientKickMessage");
            }
            else {
                 configuration.setProperty("ForceSinglePlayerClientKickMessage", kickMessage);
            }
            
            if (configuration.getProperty("AutoUpdate") != null) {
                autoUpdate = configuration.getBoolean("AutoUpdate", true);
            }
            else {
                configuration.setProperty("AutoUpdate", true);
            }
            
            if (!configuration.save()) {
                throw new IOException();
            }
        } catch (Exception e) {
            Logger.getLogger("minecraft").severe("[BukkitContrib] Failed to read configuration!");
        }
    }
    
    public static boolean isForceClient() {
        return forceClient;
    }
    
    public static boolean isAutoUpdate() {
        return autoUpdate;
    }
    
    public static String getKickMessage() {
        return kickMessage;
    }

}
