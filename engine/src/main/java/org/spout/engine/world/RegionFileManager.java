/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.io.bytearrayarray.BAAWrapper;

public class RegionFileManager {
	/**
	 * The segment size to use for chunk storage. The actual size is 2^(SEGMENT_SIZE)
	 */
	private final int SEGMENT_SIZE = 8;
	/**
	 * The timeout for the chunk storage in ms. If the store isn't accessed within that time, it can be automatically shutdown
	 */
	public static final int TIMEOUT = 30000;
	private final File regionDirectory;
	private final ConcurrentHashMap<String, BAAWrapper> cache = new ConcurrentHashMap<String, BAAWrapper>();
	private final TimeoutThread timeoutThread;

	public RegionFileManager(File worldDirectory) {
		this(worldDirectory, "region");
	}

	public RegionFileManager(File worldDirectory, String prefix) {
		this.regionDirectory = new File(worldDirectory, prefix);
		this.regionDirectory.mkdirs();
		this.timeoutThread = new TimeoutThread(worldDirectory);
		this.timeoutThread.start();
	}

	public BAAWrapper getBAAWrapper(int rx, int ry, int rz) {
		String filename = getFilename(rx, ry, rz);
		BAAWrapper regionFile = cache.get(filename);
		if (regionFile != null) {
			return regionFile;
		}
		File file = new File(regionDirectory, filename);
		regionFile = new BAAWrapper(file, SEGMENT_SIZE, SpoutRegion.CHUNKS.VOLUME, TIMEOUT);
		BAAWrapper oldRegionFile = cache.putIfAbsent(filename, regionFile);
		if (oldRegionFile != null) {
			return oldRegionFile;
		}
		return regionFile;
	}

	/**
	 * Gets the DataOutputStream corresponding to a given Chunk Snapshot.<br> <br> WARNING: This block will be locked until the stream is closed
	 *
	 * @param c the chunk snapshot
	 * @return the DataOutputStream
	 */
	public OutputStream getChunkOutputStream(ChunkSnapshot c) {
		int rx = c.getX() >> Region.CHUNKS.BITS;
		int ry = c.getY() >> Region.CHUNKS.BITS;
		int rz = c.getZ() >> Region.CHUNKS.BITS;
		return getBAAWrapper(rx, ry, rz).getBlockOutputStream(SpoutRegion.getChunkKey(c.getX(), c.getY(), c.getZ()));
	}

	public void stopTimeoutThread() {
		timeoutThread.interrupt();
	}

	public void closeAll() {
		timeoutThread.interrupt();
		try {
			timeoutThread.join();
		} catch (InterruptedException ie) {
			Spout.getLogger().info("Interrupted when trying to stop RegionFileManager timeout thread");
		}
		for (BAAWrapper regionFile : cache.values()) {
			if (!regionFile.attemptClose()) {
				Spout.getLogger().info("Unable to close region file " + regionFile.getFilename());
			}
		}
	}

	private static String getFilename(int rx, int ry, int rz) {
		return "reg" + rx + "_" + ry + "_" + rz + ".spr";
	}

	private class TimeoutThread extends Thread {
		public TimeoutThread(File worldDirectory) {
			super("Region File Manager Timeout Thread - " + worldDirectory.getPath());
			setDaemon(true);
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				int files = cache.size();
				if (files <= 0) {
					try {
						Thread.sleep(TIMEOUT >> 1);
					} catch (InterruptedException ie) {
						return;
					}
					continue;
				}
				int cnt = 0;
				long start = System.currentTimeMillis();
				for (BAAWrapper regionFile : cache.values()) {
					regionFile.timeoutCheck();
					cnt++;
					long currentTime = System.currentTimeMillis();
					long expiredTime = currentTime - start;
					long idealTime = (cnt * ((long) TIMEOUT)) / files / 2;
					long excessTime = idealTime - expiredTime;
					if (excessTime > 0) {
						try {
							Thread.sleep(excessTime);
						} catch (InterruptedException ie) {
							return;
						}
					} else if (isInterrupted()) {
						return;
					}
				}
			}
		}
	}
}
