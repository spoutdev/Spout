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
package org.spout.engine.world.pregen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

/**
 * Generates chunks in regions that do not have all the chunks already generated and saves them to disk
 */
public class WorldPregenThread extends Thread{
	private final SpoutWorld world;
	private final Random rand = new Random();
	public WorldPregenThread(SpoutWorld world) {
		super("World Pregen Thread");
		setDaemon(true);
		this.world = world;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			return;
		}
		while(!this.isInterrupted()) {
			List<Region> regions = new ArrayList<Region>(world.getRegions());
			SpoutRegion region = (SpoutRegion) regions.get(rand.nextInt(regions.size()));
			for (int cx = 0; cx < Region.CHUNKS.SIZE; cx++) {
				for (int cy = 0; cy < Region.CHUNKS.SIZE; cy++) {
					for (int cz = 0; cz < Region.CHUNKS.SIZE; cz++) {
						if (region.getChunk(cx, cy, cz, LoadOption.NO_LOAD) == null) {
							Chunk chunk = region.getChunk(cx, cy, cz, LoadOption.GEN_ONLY);
							chunk.unload(true);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								return;
							}
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
