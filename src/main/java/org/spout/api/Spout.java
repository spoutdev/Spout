/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.spout.api.command.CommandManager;
import org.spout.api.event.EventManager;
import org.spout.api.plugin.PluginManager;
import org.spout.api.resource.FileSystem;
import org.spout.api.scheduler.Scheduler;

/**
 * Represents the Spout core, to get singleton {@link Engine} instance
 */
public final class Spout {
	private final static Logger logger = Logger.getLogger("Spout");
	private static Engine instance = null;

	private Spout() {
		throw new IllegalStateException("Can not construct Spout instance");
	}

	/**
	 * Gets the {@link Logger} instance that is used to write to the console.
	 *
	 * @return logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	public static void finest(String log, Throwable t) {
		logger.log(Level.FINEST, log, t);
	}

	public static void finest(String log, Object ...params) {
		logger.log(Level.FINEST, log, params);
	}

	public static void finer(String log, Throwable t) {
		logger.log(Level.FINER, log, t);
	}

	public static void finer(String log, Object ...params) {
		logger.log(Level.FINER, log, params);
	}

	public static void fine(String log, Throwable t) {
		logger.log(Level.FINE, log, t);
	}

	public static void fine(String log, Object ...params) {
		logger.log(Level.FINE, log, params);
	}

	public static void info(String log, Throwable t) {
		logger.log(Level.INFO, log, t);
	}

	public static void info(String log, Object ...params) {
		logger.log(Level.INFO, log, params);
	}

	public static void warn(String log, Throwable t) {
		logger.log(Level.WARNING, log, t);
	}

	public static void warn(String log, Object ...params) {
		logger.log(Level.WARNING, log, params);
	}

	public static void severe(String log, Throwable t) {
		logger.log(Level.SEVERE, log, t);
	}

	public static void severe(String log, Object ...params) {
		logger.log(Level.SEVERE, log, params);
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
	 * Gets an abstract representation of the engine's {@link FileSystem}.<br/>
	 * <br/>
	 * The Filesystem handles the loading of all resources.<br/>
	 * <br/>
	 * On the client, loading a resource will load the resource from the harddrive.<br/>
	 * On the server, it will notify all clients to load the resource, as well as provide a representation of that resource.
	 * 
	 * @return filesystem from the engine.
	 */
	public static FileSystem getFileSystem() {
		return instance.getFileSystem();
	}

	/**
	 * Returns the engine's specified CommandManager.
	 *
	 * @return engine command manager
	 */
	public static CommandManager getCommandManager() {
		return instance.getCommandManager();
	}
}
