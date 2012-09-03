/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.protocol.builtin.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.ClientWorld;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.builtin.message.ChunkDataMessage;

public class ChunkDataMessageHandler extends MessageHandler<ChunkDataMessage> {
	@Override
	public void handleClient(Session session, ChunkDataMessage message) {
		if(!session.hasPlayer()) {
			return;
		}

		ClientWorld world = (ClientWorld) ((Client) session.getEngine()).getDefaultWorld();
		Class<? extends BiomeManager> managerClass;
		try {
			Class<?> testClass = Class.forName(message.getBiomeManagerClass());
			if (!BiomeManager.class.isAssignableFrom(testClass)) {
				throw new IllegalArgumentException("Biome manager class "+ testClass + " is not a BiomeManager");
			}
			managerClass = testClass.asSubclass(BiomeManager.class);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknown biome manager class: " + message.getBiomeManagerClass());
		}
		if (Spout.debugMode()) {
			Spout.getLogger().log(Level.INFO, "Recieved Chunk Data: {0}", message.toString());
		}
		BiomeManager manager;
		try {
			manager = managerClass.getConstructor(int.class, int.class, int.class).newInstance(message.getX(), message.getY(), message.getZ());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		manager.deserialize(message.getBiomeData());
		world.addChunk(message.getX(), message.getY(), message.getZ(), message.getBlockIds(), message.getBlockData(), message.getBlockLight(), message.getSkyLight(), manager);
	}
}
