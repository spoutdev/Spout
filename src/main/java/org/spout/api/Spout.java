/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api;

import java.util.logging.Logger;

import org.spout.api.event.EventManager;
import org.spout.api.plugin.Platform;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.plugin.PluginManager;

/**
 * Represents the Spout core, to get singleton {@link Engine} instance
 *
 */
public final class Spout {
	private static Engine instance = null;

	private Spout() {
		throw new IllegalStateException("Can not construct Spout instance");
	}

	public static void setEngine(Engine game) {
		if (instance == null) {
			instance = game;
		} else {
			throw new UnsupportedOperationException("Can not redefine singleton Game instance");
		}
	}

	/**
	 * Gets the currently running engine instance.
	 * 
	 * @return engine
	 */
	public static Engine getEngine() {
		return instance;
	}

	/**
	 * Gets the {@link Logger} instance that is used to write to the console.
	 *
	 * @return logger
	 */
	public static Logger getLogger() {
		if (instance == null) {
			return Logger.getLogger("");
		}
		return instance.getLogger();
	}

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.<br/>
	 * <br/>
	 * Players will be sent a default disconnect message.
	 */
	public static void stop() {
		instance.stop();
	}

	/**
	 * Returns the game's {@link EventManager} Event listener registration and
	 * calling is handled through this.
	 *
	 * @return Our EventManager instance
	 */
	public static EventManager getEventManager() {
		return instance.getEventManager();
	}
	
	/**
	 * Returns the game's {@link PluginManager}
	 * 
	 * @return Our PluginManager instance
	 */
	 public static PluginManager getPluginManager() {
	 	return instance.getPluginManager();
	 }
	
	/**
	 * Returns the {@link Platform} that the game is currently running on.
	 *
	 * @return current platform type
	 */
	public static Platform getPlatform() {
		return instance.getPlatform();
	}

	/**
	 * Gets the scheduler
	 *
	 * @return the scheduler
	 */
	public static Scheduler getScheduler() {
		return instance.getScheduler();
	}

	/**
	 * Returns true if the game is running in debug mode <br/>
	 * <br/>
	 * To start debug mode, start Spout with -debug
	 * 
	 * @return true if server is started with the -debug flag, false if not
	 */
	public static boolean debugMode() {
		return instance.debugMode();
	}

	/**
	 * Logs the given string using {@Link Logger#info(String)} to the default logger instance.
	 * 
	 * @param arg to log
	 */
	public static void log(String arg) {
		getLogger().info(arg);
	}

	/**
	 * Returns the String version of the API.
	 * 
	 * @return version
	 */
	public static String getAPIVersion() {
		return instance.getClass().getPackage().getImplementationVersion();
	}

	/**
	 * Gets an abstract representation of the engine's {@link Filesystem}.<br/>
	 * <br/>
	 * The Filesystem handles the loading of all resources.<br/>
	 * <br/>
	 * On the client, loading a resource will load the resource from the harddrive.<br/>
	 * On the server, it will notify all clients to load the resource, as well as provide a representation of that resource.
	 * 
	 * @return filesystem from the engine.
	 */
	public static FileSystem getFilesystem() {
		return instance.getFilesystem();
	}


	/**
	 * Outputs multiple lines to the logger using '\n' as the line-ending character.
	 * This will use {@Link Logger#info(String)} to dump each line.
	 * 
	 * @param multiline
	 */
	public void multilineLog(String multiline) {
		String[] split = multiline.split("\n");
		for (String line : split) {
			while (line.length() > 40) {
				getLogger().info(line.substring(0, 40));
				line = line.substring(40);
			}
			getLogger().info(line);
		}
	}
}
