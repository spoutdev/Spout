package org.getspout.spout.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.util.config.Configuration;
import org.getspout.spout.Spout;

public class ConfigReader {
	private static boolean forceClient = false;
	private static boolean autoUpdate = false;
	private static int authTicks = 200;
	private static String kickMessage = "This server requires Spoutcraft! http://bit.ly/unleashtheflow";
	private static boolean allowVisualCheats = false;
	private static boolean chunkDataCache = true;
	
	public void read() {
		try {
			File directory = Spout.getInstance().getDataFolder();
			if (!directory.exists()) {
				try {
					directory.mkdir();
				}
				catch (SecurityException e1) {}
			}
			File config = new File(directory, "config.yml");
			if (!config.exists()) {
				try {
					config.createNewFile();
				}
				catch (SecurityException e1) {}
			}
			Configuration configuration = Spout.getInstance().getConfiguration();
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
			
			if (configuration.getProperty("AuthenticateTicks") != null) {
				authTicks = configuration.getInt("AuthenticateTicks", 200);
			}
			else {
				 configuration.setProperty("AuthenticateTicks", authTicks);
			}
			
			if (configuration.getProperty("AutoUpdate") != null) {
				autoUpdate = configuration.getBoolean("AutoUpdate", true);
			}
			else {
				configuration.setProperty("AutoUpdate", true);
			}
			
			if (configuration.getProperty("AllowVisualCheats") != null) {
				allowVisualCheats = configuration.getBoolean("AllowVisualCheats", false);
			}
			else {
				configuration.setProperty("AllowVisualCheats", false);
			}
			
			if (configuration.getProperty("ChunkDataCache") != null) {
				chunkDataCache = configuration.getBoolean("ChunkDataCache", true);
			} else {
				configuration.setProperty("ChunkDataCache", true);
			}
			
			if (!configuration.save()) {
				throw new IOException();
			}
		} catch (Exception e) {
			Logger.getLogger("minecraft").severe("[Spout] Failed to read configuration!");
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
	
	public static int getAuthenticateTicks() {
		return authTicks;
	}
	
	public static boolean isAllowVisualCheats() {
		return allowVisualCheats;
	}
	
	public static boolean isChunkDataCache() {
		return chunkDataCache;
	}
}
