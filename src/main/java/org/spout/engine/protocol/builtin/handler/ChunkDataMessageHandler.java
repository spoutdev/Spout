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
package org.spout.engine.protocol.builtin.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.World;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.ClientSession;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;
import org.spout.engine.world.SpoutClientWorld;

public class ChunkDataMessageHandler extends MessageHandler<ChunkDataMessage> {
	@Override
	public void handleClient(ClientSession session, ChunkDataMessage message) {
		World world = session.getEngine().getDefaultWorld();
		if (message.isUnload()) {
			((SpoutClientWorld) world).removeChunk(message.getX(), message.getY(), message.getZ());
			return;
		}
		if (message.hasBiomes()) {
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
		}
		((SpoutClientWorld) world).addChunk(message.getX(), message.getY(), message.getZ(), message.getBlockIds(), message.getBlockData());
	}
}
