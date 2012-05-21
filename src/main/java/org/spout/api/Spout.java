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

	public static Engine getEngine() {
		return instance;
	}

	public static Logger getLogger() {
		return instance.getLogger();
	}

	public static void stop() {
		instance.stop();
	}

	public static EventManager getEventManager() {
		return instance.getEventManager();
	}

	public static Platform getPlatform() {
		return instance.getPlatform();
	}

	public static Scheduler getScheduler() {
		return instance.getScheduler();
	}

	public static boolean debugMode() {
		return instance.debugMode();
	}

	public static void log(String arg) {
		instance.getLogger().info(arg);
	}

	public static String getAPIVersion() {
		return instance.getClass().getPackage().getImplementationVersion();
	}
}
