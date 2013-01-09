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
package org.spout.engine.world;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.engine.scheduler.SpoutScheduler;

public class WorldGeneratorThread extends Thread {
	
	private static final WorldGeneratorThread instance = new WorldGeneratorThread();
	private static volatile SpoutScheduler scheduler; 
	
	private BlockingQueue<Point> queue = new LinkedBlockingQueue<Point>();
	
	public WorldGeneratorThread() {
		super("World Generator Thread");
	}
	
	public static void submitRegion(Region r) {
		instance.queue.add(r.getBase());
	}
	
	public static void startThread() {
		instance.start();
	}
	
	public static void finish() {
		instance.interrupt();
	}
	
	public static void staticJoin() {
		try {
			instance.join();
		} catch (InterruptedException ie) {
			Spout.getLogger().info("Main thread interruped while waiting for world generator thread to end");
		}
	}
	
	public void run() {
		
		scheduler = ((SpoutScheduler) Spout.getScheduler());
		
		mainLoop:
		while (!isInterrupted()) {
			
			boolean debug = Spout.getEngine().debugMode();
			
			Point p;
			try {
				p = queue.take();
			} catch (InterruptedException e) {
				break mainLoop;
			}
			final SpoutWorld world = (SpoutWorld) p.getWorld();
			final int rx = p.getBlockX() >> Region.BLOCKS.BITS;
			final int ry = p.getBlockY() >> Region.BLOCKS.BITS;
			final int rz = p.getBlockZ() >> Region.BLOCKS.BITS;
			final Point base = p;

			SpoutRegion r = scheduler.coreSafeCall("World generator thread get region", new Callable<SpoutRegion>() {
				public SpoutRegion call() {
					return ((SpoutWorld) base.getWorld()).getRegion(rx, ry, rz, LoadOption.LOAD_GEN);
				}
			});
			long start = System.currentTimeMillis();
			final CuboidBlockMaterialBuffer buffer = r.generateChunks();
			if (buffer == null) {
				continue;
			}
			if (debug) {
				Spout.getLogger().info("Region " + r + " generated (" + (System.currentTimeMillis() - start) + " ms)");
				Spout.getLogger().info("Copying buffer to Region " + r);
			}
			int cx = r.getChunkX();
			int cy = r.getChunkY();
			int cz = r.getChunkZ();
			for (int x = cx; x < cx + Region.CHUNKS.SIZE; x++) {
				for (int z = cz; z < cz + Region.CHUNKS.SIZE; z++) {
					for (int y = cy; y < cy + Region.CHUNKS.SIZE; y++) {
						final int finalX = x;
						final int finalY = y;
						final int finalZ = z;
						scheduler.coreSafeRun("World generator thread buffer copy", new Runnable(){
							public void run() {
								SpoutRegion region = ((SpoutWorld) base.getWorld()).getRegion(rx, ry, rz, LoadOption.LOAD_GEN);
								region.copyChunksFromBufferIfNotGenerated(world, buffer, finalX, finalY, finalZ);
							}
						});
					}
					while (scheduler.isServerOverloaded()) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							break mainLoop;
						}
					}
				}
			}
			if (debug) {
				Spout.getLogger().info("Copy complete, " + r);
			}
		}
		
	}
	
}
