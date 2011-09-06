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
package org.getspout.spout.chunkstore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.World;
import org.getspout.spout.util.ChunkUtil;

public class ChunkStore {

	HashMap<UUID, HashMap<Long, SimpleRegionFile>> regionFiles = new HashMap<UUID, HashMap<Long, SimpleRegionFile>>();

	public void closeAll() {
		for (UUID uid : regionFiles.keySet()) {
			HashMap<Long, SimpleRegionFile> worldRegions = regionFiles.get(uid);
			Iterator<SimpleRegionFile> itr = worldRegions.values().iterator();
			while (itr.hasNext()) {
				SimpleRegionFile rf = itr.next();
				if (rf != null) {
					rf.close();
					itr.remove();
				}
			}
		}
		regionFiles = new HashMap<UUID, HashMap<Long, SimpleRegionFile>>();
	}

	public ChunkMetaData readChunkMetaData(World world, int x, int z) throws IOException {
		SimpleRegionFile rf = getSimpleRegionFile(world, x, z);
		InputStream in = rf.getInputStream(x, z);
		if (in == null) {
			return null;
		}
		ObjectInputStream objectStream = new ObjectInputStream(in);
		try {
			Object o = objectStream.readObject();
			if (o instanceof ChunkMetaData) {
				return (ChunkMetaData) o;
			} else {
				throw new RuntimeException("Wrong class type read for chunk meta data for " + x + ", " + z);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to process chunk meta data for " + x + ", " + z, e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to find serialized class for " + x + ", " + z, e);
		}
	}

	public void writeChunkMetaData(World world, int x, int z, ChunkMetaData data) {

		if (!data.getDirty()) {
			return;
		}
		try {
			SimpleRegionFile rf = getSimpleRegionFile(world, x, z);
			ObjectOutputStream objectStream = new ObjectOutputStream(rf.getOutputStream(x, z));
			objectStream.writeObject(data);
			objectStream.flush();
			objectStream.close();
			data.setDirty(false);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write chunk meta data for " + x + ", " + z, e);
		}
	}

	private SimpleRegionFile getSimpleRegionFile(World world, int x, int z) {

		File directory = new File(Utils.getWorldDirectory(world), "spout_meta");

		directory.mkdirs();

		UUID key = world.getUID();

		HashMap<Long, SimpleRegionFile> worldRegions = regionFiles.get(key);

		if (worldRegions == null) {
			worldRegions = new HashMap<Long, SimpleRegionFile>();
			regionFiles.put(key, worldRegions);
		}

		int rx = x >> 5;
		int rz = z >> 5;

		long key2 = ChunkUtil.intPairToLong(rx, rz);

		SimpleRegionFile regionFile = worldRegions.get(key2);

		if (regionFile == null) {

			File file = new File(directory, "spout_" + rx + "_" + rz + "_.spm");
			regionFile = new SimpleRegionFile(file, rx, rz);
			worldRegions.put(key2, regionFile);

		}

		return regionFile;

	}

}
