package org.getspout.spout.chunkstore;

import java.io.Serializable;
import java.util.HashMap;

public class ChunkMetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	// This data is saved.  This means data can handle different map heights\
	
	private int cx;
	private int cz;
	
	private int[] blockX = new int[1];
	private int[] blockY = new int[1];
	private int[] blockZ = new int[1];
	@SuppressWarnings("unchecked")
	private HashMap<String,byte[]>[] blockData = new HashMap[1];
	
	private int blocks = 0;
	
	
	// This is just a fast lookup for the data
	volatile private HashMap<Integer,HashMap<String,byte[]>> fastLookup = null;
	volatile private HashMap<Integer,Integer> indexLookup = null;
	
	volatile private boolean firstRead = true;
	
	private int positionToKey(int x, int y, int z) {
		
		if (firstRead) {
			firstRead = false;
			updateHashMap();
		}
		
		int xx = x & 0xF;
		int yy = y & 0xFF;
		int zz = z & 0xF;
		
		return (xx << 24) | (yy << 8) | (zz << 0);
	}
	
	private void updateHashMap() {
		for (int i = 0; i < blocks; i++) {
			int key = positionToKey(blockX[i], blockY[i], blockZ[i]);
			fastLookup.put(key, blockData[i]);
			indexLookup.put(key, i);
		}
	}
	
	public Object getData(String id, int x, int y, int z) {
		
		int key = positionToKey(x, y, z);
		
		HashMap<String,byte[]> blockData = fastLookup.get(key);
		
		if (blockData == null) {
			return null;
		} else {
			byte[] serial = blockData.get(id);
			return Utils.deserialize(serial);
		}
		
	}
	
	public void putData(String id, int x, int y, int z, Object o) {
		
		int key = positionToKey(x, y, z);
		
		HashMap<String,byte[]> blockData = fastLookup.get(key);
		
		if (blockData == null) {
			
			if (blocks >= blockX.length) {
				
			}
			
		}
		
	}
	
	public void resizeArrays(int size) {
		
		if (size < blocks) {
			throw new IllegalArgumentException("Attempted to reduce array size below its limit");
		}
		
		int[] oldBlockX = blockX;
		int[] oldBlockY = blockY;
		int[] oldBlockZ = blockZ;
		@SuppressWarnings("unchecked")
		HashMap<String,byte[]>[] oldBlockData = blockData;
		
		blockX = new int[size];
		blockY = new int[size];
		blockZ = new int[size];

		blockData = new HashMap[size];
		
		int dest = 0;
		for (int i = 0; i < blocks; i++) {
			if (dest < size) {
				blockX[dest] = oldBlockX[i];
				blockY[dest] = oldBlockY[i];
				
			}
		}
		
	}

}
