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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.getspout.spout.util.ChunkUtil;
import org.getspout.spout.util.UniqueStringMap;
import org.getspout.spoutapi.chunkdatamanager.ChunkDataManager;

public class SimpleChunkDataManager implements ChunkDataManager {
	
	private ChunkStore chunkStore = new ChunkStore();
	
	private HashMap<UUID,HashMap<Long,ChunkMetaData>> chunkMetaDataLoaded = new HashMap<UUID,HashMap<Long,ChunkMetaData>>();
	
	public void closeAllFiles() {
		chunkStore.closeAll();
	}

	public ChunkMetaData loadChunk(Chunk c) {
		return loadChunk(c.getWorld(), c.getX(), c.getZ());
	}
	
	public ChunkMetaData loadChunk(World world, int x, int z) {
		
		ChunkMetaData md = getMetaData(world, x, z, false);
		
		if (md == null) {
			md = getMetaData(world, x, z, true);
		}
		
		return md;

	}
	
	public boolean loadWorldChunks(World w) {
		
		Chunk[] chunks = w.getLoadedChunks();
		
		boolean loaded = false;
		
		for (Chunk c : chunks) {
			loaded |= loadChunk(c) != null;
		}
		
		return loaded;
		
	}
	
	public boolean loadAllChunks() {
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		boolean loaded = false;
		
		for (World w : worlds) {
			loaded |= loadWorldChunks(w);
		}
		
		return loaded;	
	}
	
	public boolean unloadChunk(Chunk c) {
		
		return saveChunk(c.getWorld(), c.getX(), c.getZ(), true);
		
	}
	
	public boolean unloadChunk(World w, int x, int z) {
		return saveChunk(w, x, z, true);
	}
	
	public boolean saveChunk(Chunk c) {
		return saveChunk(c.getWorld(), c.getX(), c.getZ(), false);
	}
		
	public boolean saveChunk(World w, int x, int z, boolean unload) {
		
		ChunkMetaData md = getMetaData(w, x, z, false);
		
		if (md != null) {
			chunkStore.writeChunkMetaData(w, x, z, md);
			if (unload) {
				chunkMetaDataLoaded.get(w.getUID()).remove(ChunkUtil.intPairToLong(x, z));
			}
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean unloadWorldChunks(World world) {
		return saveWorldChunks(world, true);
	}
	
	public boolean saveWorldChunks(World world) {
		return saveWorldChunks(world, false);
	}
	
	public boolean saveWorldChunks(World world, boolean unload) {
		
		HashMap<Long,ChunkMetaData> worldChunks = chunkMetaDataLoaded.get(world.getUID());
		
		if (worldChunks == null) {
			return false;
		}
		
		Collection<ChunkMetaData> chunks = new ArrayList<ChunkMetaData>(worldChunks.values());
		
		boolean unloaded = false;
		for (ChunkMetaData md : chunks) {
			unloaded |= unloadChunk(world, md.getChunkX(), md.getChunkZ());
		}
		
		return unloaded;
	}
	
	public boolean unloadAllChunks() {
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		boolean unloaded = false;

		for(World world : worlds) {
			unloaded |= unloadWorldChunks(world);
		}
		
		return unloaded;
	}
	
	public int getStringId(String string) {
		return UniqueStringMap.getId(string);
	}


	public Serializable setBlockData(String id, World world, int x, int y, int z, Serializable data) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, false);
		
		return md.putBlockData(id, x, y, z, data);
		
	}


	public Serializable getBlockData(String id, World world, int x, int y, int z) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, false);
		
		return md.getBlockData(id, x, y, z);
	}


	public Serializable removeBlockData(String id, World world, int x, int y, int z) {
		
		ChunkMetaData md = getMetaData(world, x >> 4, z >> 4, false);
		
		return md.removeBlockData(id, x, y, z);
	}


	public Serializable setChunkData(String id, World world, int x, int z, Serializable data) {

		ChunkMetaData md = getMetaData(world, x, z, false);
		
		return md.putChunkData(id, data);
	}
	

	public Serializable getChunkData(String id, World world, int x, int z) {

		ChunkMetaData md = getMetaData(world, x, z, false);
		
		return md.getChunkData(id);
	}


	public Serializable removeChunkData(String id, World world, int x, int z) {

		ChunkMetaData md = getMetaData(world, x, z, false);
		
		return md.removeChunkData(id);
	}
	
	private ChunkMetaData getMetaData(World world, int x, int z, boolean load) {
		long key = ChunkUtil.intPairToLong(x, z);
		UUID uid = world.getUID();
		HashMap<Long,ChunkMetaData> worldChunks = chunkMetaDataLoaded.get(uid);
		if (worldChunks == null) {
			worldChunks = new HashMap<Long,ChunkMetaData>();
			chunkMetaDataLoaded.put(uid, worldChunks);
		}
		
		ChunkMetaData md = worldChunks.get(ChunkUtil.intPairToLong(x, z));
		
		if (md == null && load) {
			try {
				md = chunkStore.readChunkMetaData(world, x, z);
				if (md != null) {
					if (!md.getWorldUID().equals(world.getUID()) || md.getChunkX() != x || md.getChunkZ() != z) {
						System.out.println("Expected: " + world.getUID() + " " + x + " " + z);
						System.out.println("Actual: " + md.getWorldUID() + " " + md.getChunkX() + " " + md.getChunkZ());
						throw new RuntimeException("Chunk meta data stored in wrong location");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			if (md == null) {
				md = new ChunkMetaData(world.getUID(), x, z);
			}
			
			worldChunks.put(key, md);
		}
		
		return md;
	}
	
}
