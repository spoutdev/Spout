/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.util.config.Configuration;
import org.getspout.spout.Spout;

@SuppressWarnings("deprecation")
public class ConfigReader {
	private static boolean forceClient = false;
	private static boolean autoUpdate = false;
	private static int authTicks = 200;
	private static String kickMessage = "This server requires Spoutcraft! http://bit.ly/unleashtheflow";
 
	private static boolean allowSkyCheat = false;
	private static boolean allowClearWaterCheat = false;
	private static boolean allowStarsCheat = false;
	private static boolean allowWeatherCheat = false;
	private static boolean allowTimeCheat = false;
	private static boolean allowCoordsCheat = false;
	private static boolean allowEntityLabelCheat = false;
 
	private static boolean chunkDataCache = true;
	private static boolean teleportSmoothing = true;
	private static boolean authenticateSpoutcraft = true;
	
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

			if (configuration.getProperty("AllowSkyCheat") != null) {
				allowSkyCheat = configuration.getBoolean("AllowSkyCheat", false);
			}
			else {
				configuration.setProperty("AllowSkyCheat", false);
			}

			if (configuration.getProperty("AllowClearWaterCheat") != null) {
				allowClearWaterCheat = configuration.getBoolean("AllowClearWaterCheat", false);
			}
			else {
				configuration.setProperty("AllowClearWaterCheat", false);
			}

			
			if (configuration.getProperty("AllowStarsCheat") != null) {
				allowStarsCheat = configuration.getBoolean("AllowStarsCheat", false);
			}
			else {
				configuration.setProperty("AllowStarsCheat", false);
			}

			if (configuration.getProperty("AllowWeatherCheat") != null) {
				allowWeatherCheat = configuration.getBoolean("AllowWeatherCheat", false);
			}
			else {
				configuration.setProperty("AllowWeatherCheat", false);
			}

			if (configuration.getProperty("AllowTimeCheat") != null) {
				allowTimeCheat = configuration.getBoolean("AllowTimeCheat", false);
			}
			else {
				configuration.setProperty("AllowTimeCheat", false);
			}
			
			if (configuration.getProperty("AllowCoordsCheat") != null) {
				allowCoordsCheat = configuration.getBoolean("AllowCoordsCheat", false);
			}
			else {
				configuration.setProperty("AllowCoordsCheat", false);
			}

			if (configuration.getProperty("AllowEntityLabelCheat") != null) {
				allowEntityLabelCheat = configuration.getBoolean("AllowEntityLabelCheat", false);
			}
			else {
				configuration.setProperty("AllowEntityLabelCheat", false);
			}	 					
			
			if (configuration.getProperty("ChunkDataCache") != null) {
				chunkDataCache = configuration.getBoolean("ChunkDataCache", true);
			} else {
				configuration.setProperty("ChunkDataCache", true);
			}
			
			if (configuration.getProperty("TeleportSmoothing") != null) {
				teleportSmoothing = configuration.getBoolean("TeleportSmoothing", true);
			} else {
				configuration.setProperty("TeleportSmoothing", true);
			}
			
			if (configuration.getProperty("AuthenticateSpoutcraft") != null) {
				authenticateSpoutcraft = configuration.getBoolean("AuthenticateSpoutcraft", true);
			} else {
				configuration.setProperty("AuthenticateSpoutcraft", true);
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
 
	public static boolean isAllowSkyCheat() {
	 return allowSkyCheat;
	}
	
	public static boolean isAllowClearWaterCheat() {
	 return allowClearWaterCheat;
	}
	
	public static boolean isAllowStarsCheat() {
		return allowStarsCheat;
	}
	
	public static boolean isAllowWeatherCheat() {
		return allowWeatherCheat;
	}
	
	public static boolean isAllowTimeCheat() {
		return allowTimeCheat;
	}
	
	public static boolean isAllowCoordsCheat() {
		return allowCoordsCheat;
	}
	
	public static boolean isAllowEntityLabelCheat() {
		return allowEntityLabelCheat;
	}	
	
	public static boolean isChunkDataCache() {
		return chunkDataCache;
	}
	
	public static boolean isTeleportSmoothing() {
		return teleportSmoothing;
	}
	
	public static boolean authenticateSpoutcraft() {
		return authenticateSpoutcraft;
	}
}
