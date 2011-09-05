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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.util.BlockVector;

public class ChunkMetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	// This data is saved.  This means data can handle different map heights
	// Changes may be needed to the positionToKey method
	
	private int cx;
	private int cz;
	private UUID worldUid;
	
	private int[] blockX = new int[1];
	private int[] blockY = new int[1];
	private int[] blockZ = new int[1];

	private HashMap<String,byte[]>[] blockData = null;
	private HashMap<String,byte[]> chunkData = null;
	@SuppressWarnings("unchecked")
	private HashMap<String,Serializable>[] blockDataAsObject = new HashMap[1];
	private HashMap<String,Serializable> chunkDataAsObject = new HashMap<String,Serializable>();
	
	private int blocks = 0;
	
	
	// This is just a fast lookup for the data
	transient private HashMap<Integer,HashMap<String,Serializable>> fastLookup = null;
	transient private HashMap<Integer,Integer> indexLookup = null;
	
	transient private boolean dirty = false;
	
	ChunkMetaData(UUID worldId, int cx, int cz) {
		this.cx = cx;
		this.cz = cz;
		this.worldUid = worldId;
	}
	
	public boolean getDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public int getChunkX() {
		return cx;
	}
	
	public int getChunkZ() {
		return cz;
	}
	
	public UUID getWorldUID() {
		return worldUid;
	}
	
	public Serializable removeChunkData(String id) {
		Serializable serial = chunkDataAsObject.remove(id);
		if (serial != null) {
			dirty = true;
			return serial;
		} else {
			return null;
		}
	}
	
	public Serializable getChunkData(String id) {
		Serializable serial = chunkDataAsObject.get(id);
		return serial;
	}
	
	public Serializable putChunkData(String id, Serializable o) {
		
		Serializable serial = chunkDataAsObject.put(id, Utils.serialize(o));

		dirty = true;
		
		return serial;
		
	}
	
	public BlockVector[] getTaggedBlocks() {
		
		int count = 0;
		
		for (int i = 0; i < blocks; i++) {
			if (blockDataAsObject[i] != null) {
				count++;
			}
		}
		
		BlockVector[] vectors = new BlockVector[count];
		
		count = 0;
		
		for (int i = 0; i < blocks; i++) {
			if (blockDataAsObject[i] != null) {
				vectors[count] = new BlockVector(blockX[count], blockY[count], blockZ[count]);
				count++;
			}
		}
		
		return vectors;
		
	}
	
	public Serializable removeBlockData(String id, int x, int y, int z) {
		
		int key = positionToKey(x, y, z);
		
		HashMap<String,Serializable> localBlockData = fastLookup.get(key);
		
		if (localBlockData == null) {
			return null;
		} else {
			dirty = true;
			Serializable serial = localBlockData.remove(id);
			if (localBlockData.size() == 0) {
				int index = indexLookup.get(key);
				blockDataAsObject[index] = null;
				fastLookup.remove(key);
				indexLookup.remove(key);
			}
			return serial;
		}
		
	}
	
	public Serializable getBlockData(String id, int x, int y, int z) {
		
		int key = positionToKey(x, y, z);
		
		HashMap<String,Serializable> localBlockData = fastLookup.get(key);
		
		if (localBlockData == null) {
			return null;
		} else {
			Serializable serial = localBlockData.get(id);
			return serial;
		}
		
	}
	
	public Serializable putBlockData(String id, int x, int y, int z, Serializable o) {
		
		int key = positionToKey(x, y, z);
		
		HashMap<String,Serializable> localBlockData = fastLookup.get(key);
		
		if (localBlockData == null) {
			if (blocks >= this.blockData.length) {
				resizeArrays(1 + ((blocks * 3) / 2));
			}
			localBlockData = new HashMap<String,Serializable>();
			blockX[blocks] = x;
			blockY[blocks] = y;
			blockZ[blocks] = z;
			blockDataAsObject[blocks] = localBlockData;
			fastLookup.put(key, blockDataAsObject[blocks]);
			indexLookup.put(key, blocks);
			blocks++;
			refreshLookup();
		}
		
		Serializable oldObject = localBlockData.put(id, Utils.serialize(o));
		dirty = true;

		return oldObject;

	}
	
	@SuppressWarnings("unchecked")
	private void resizeArrays(int size) {
		
		dirty = true;
		
		if (size < blocks) {
			throw new IllegalArgumentException("Attempted to reduce array size below its limit");
		}
		
		int[] oldBlockX = blockX;
		int[] oldBlockY = blockY;
		int[] oldBlockZ = blockZ;
		
		HashMap<String,Serializable>[] oldBlockData = blockDataAsObject;
		
		blockX = new int[size];
		blockY = new int[size];
		blockZ = new int[size];

		blockDataAsObject = new HashMap[size];
		
		int dest = 0;
		for (int i = 0; i < blocks; i++) {
			if (oldBlockData[i] != null) {
				blockX[dest] = oldBlockX[i];
				blockY[dest] = oldBlockY[i];
				blockZ[dest] = oldBlockZ[i];
				blockDataAsObject[dest] = oldBlockData[i];
				dest++;
			}
		}
		
		blocks = dest;
		
	}
	
	private int positionToKey(int x, int y, int z) {
		
		int xx = x & 0xF;
		int yy = y & 0xFF;
		int zz = z & 0xF;
		
		return (xx << 24) | (yy << 8) | (zz << 0);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshLookup() {
		if (blockData != null || chunkData != null) {
			System.out.println("[Spout] Converting chunk data to new format for chunk " + cx + ", " + cz);
			dirty = true;
			if (blockData == null || chunkData == null) {
				throw new RuntimeException("[Spout] chunk meta data error, partial conversion occured");
			}
			blockDataAsObject = new HashMap[blockData.length];
			for (int i = 0; i < blockData.length; i++) {
				HashMap<String, byte[]> map = blockData[i];
				if (map != null) {
					HashMap<String, Serializable> singleBlockData = new HashMap<String, Serializable>();
					blockDataAsObject[i] = singleBlockData;
					Iterator<Map.Entry<String, byte[]>> itr = map.entrySet().iterator();
					while (itr.hasNext()) {
						Map.Entry<String, byte[]> entry  = itr.next();
						singleBlockData.put(entry.getKey(), Utils.deserialize(entry.getValue()));
					}
				}
			}
			blockData = null;
			
			chunkDataAsObject = new HashMap<String, Serializable>();
			HashMap<String, byte[]> map = chunkData;
			if (map != null) {
				Iterator<Map.Entry<String, byte[]>> itr = map.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry<String, byte[]> entry  = itr.next();
					chunkDataAsObject.put(entry.getKey(), Utils.deserialize(entry.getValue()));
				}
			}
			chunkData = null;
		}

		fastLookup = new HashMap<Integer,HashMap<String,Serializable>>();
		indexLookup = new HashMap<Integer,Integer>();
		for (int i = 0; i < blocks; i++) {
			int key = positionToKey(blockX[i], blockY[i], blockZ[i]);
			fastLookup.put(key, blockDataAsObject[i]);
			indexLookup.put(key, i);
		}
	}

}
