package org.getspout.spout.chunkstore;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.World;

public class ChunkStore {
	
	HashMap<UUID,HashMap<Long,SimpleRegionFile>> regionFiles = new HashMap<UUID,HashMap<Long,SimpleRegionFile>>();
	
	public void closeAll() {
		for(UUID uid : regionFiles.keySet()) {
			HashMap<Long,SimpleRegionFile> worldRegions = regionFiles.get(uid);
			for(Long key : worldRegions.keySet()) {
				SimpleRegionFile rf = worldRegions.get(key);
				if(rf != null) {
					rf.close();
				}
			}
		}
		regionFiles = new HashMap<UUID,HashMap<Long,SimpleRegionFile>>();
	}
	
	public ChunkMetaData readChunkMetaData(World world, int x, int z) throws IOException {
		SimpleRegionFile rf = getSimpleRegionFile(world, x, z);
		ObjectInputStream objectStream = new ObjectInputStream(rf.getInputStream(x, z));
		try {
			Object o = objectStream.readObject();
			if(o instanceof ChunkMetaData) {
				return (ChunkMetaData)o;
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
		try {
		SimpleRegionFile rf = getSimpleRegionFile(world, x, z);
		ObjectOutputStream objectStream = new ObjectOutputStream(rf.getOutputStream(x, z));
		objectStream.writeObject(data);
		objectStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to write chunk meta data for " + x + ", " + z, e);
		}
	}
	
	public SimpleRegionFile getSimpleRegionFile(World world, int x, int z) {
		
		File directory = Utils.getWorldDirectory(world);
		
		UUID key = world.getUID();
		
		HashMap<Long,SimpleRegionFile> worldRegions = regionFiles.get(key);
		
		if (worldRegions == null) {
			worldRegions = new HashMap<Long,SimpleRegionFile>();
			regionFiles.put(key, worldRegions);
		}
		
		long key2 = intPairToLong(x, z);
		SimpleRegionFile regionFile = worldRegions.get(key2);
		
		if (regionFile == null) {
			
			int rx = x >> 5;
			int rz = z >> 5;
			File file = new File(directory, "spout_" + rx + "_" + rz + "_.spo");
			regionFile = new SimpleRegionFile(file, rx, rz);
			worldRegions.put(key2, regionFile);
			
		}
		
		return regionFile;
		
	}
	
	private static long intPairToLong(int x, int z) {
		return (((long)x)<<32) | (((long)z) & 0xFFFFFFFFL);
	}
	
}
