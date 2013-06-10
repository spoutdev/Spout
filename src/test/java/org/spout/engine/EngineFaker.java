/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;

import org.mockito.Mockito;

import org.spout.api.Engine;
import org.spout.api.Engine;
import org.spout.api.Platform;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.Spout;
import org.spout.api.event.Event;
import org.spout.api.event.EventExecutor;
import org.spout.api.event.EventManager;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.lang.PluginDictionary;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.PluginDescriptionFile;
import org.spout.api.resource.FileSystem;

@SuppressWarnings("deprecation")
public class EngineFaker {
	public static final UUID TEST_UUID = UUID.fromString("86981616-5a22-4a5a-8a7c-c6675ff5672a");
	private final static Server engineInstance;

	static {
		Server server = Mockito.mock(Server.class);
		FileSystem filesystem = Mockito.mock(FileSystem.class);
		Mockito.when(server.getPlatform()).thenReturn(Platform.SERVER);
		Mockito.when(server.getFileSystem()).thenReturn(filesystem);
		Mockito.when(server.getEventManager()).thenReturn(new TestEventManager());
		Mockito.when(server.getLogger()).thenReturn(Mockito.mock(Logger.class));
		World setupWorld = WorldFaker.setupWorld();
		Mockito.when(server.getWorld(TEST_UUID)).thenReturn(setupWorld);

		TestPlugin plugin = new TestPlugin();
		plugin.initialize(null, server, new PluginDescriptionFile("TestPlugin", "dev", "org.spout.api.TestPlugin", "all"), null, null, null);
		TestPlugin.instance = plugin;

		Spout.setEngine(server);
		engineInstance = server;
	}

	public static Server setupEngine() {
		return engineInstance;
	}
	
	public static void main(String[] args) {
		
	}
	private static class TestPlugin extends CommonPlugin {
		public static TestPlugin instance;
		@Override
		public void onEnable() {
		}

		@Override
		public void onDisable() {
		}

		@Override
		public void onReload() {
		}

		@Override
		public WorldGenerator getWorldGenerator(String world, String generator) {
			return null;
		}

		@Override
		public InputStream getResource(String path) {
			return null;
		}

		@Override
		public void extractResource(String path, File destination) throws IOException {
		}

		@Override
		public PluginDictionary getDictionary() {
			return null;
		}
	}

	private static class TestEventManager implements EventManager {
		@Override
		public <T extends Event> T callEvent(T event) {
			return event;
		}

		@Override
		public <T extends Event> void callDelayedEvent(T event) {

		}

		@Override
		public void registerEvents(Listener listener, Object owner) {

		}

		@Override
		public void registerEvent(Class<? extends Event> event, Order priority, EventExecutor executor, Object owner) {

		}
	}
}
