package org.getspout.spout.block;

import java.util.HashMap;

public class CraftCustomMaterial {

	private final static CraftCustomMaterial[][] cache;
	private final static String[] nameCache;
	private final static HashMap<String, Integer> nameLookup = new HashMap<String, Integer>();
	
	static {
		
		cache = new CraftCustomMaterial[65536][];
		nameCache = new String[65536];
	}
	
	private final String name;
	private final int blockId;
	private final int metaData;
	
	private CraftCustomMaterial(int blockId, int metaData, String name) {
		this.blockId = blockId;
		this.metaData = metaData;
		this.name = name;
	}
	
	public static boolean registerMaterial(int blockId, int metaDataSize, String name) {
		if (cache[blockId] != null) {
			return false;
		} else if (blockId < 1024 || blockId > 65536 || metaDataSize > 256 || metaDataSize < 0) {
			throw new IllegalArgumentException("[Spout] Illegal parameters for custom material registration");
		}
		
		cache[blockId] = new CraftCustomMaterial[metaDataSize];
		nameCache[blockId] = name;
		nameLookup.put(name, blockId);
		
		return true;
	}
	
	public static CraftCustomMaterial getMaterial(int blockId, int metaData) {
		
		CraftCustomMaterial[] materialType = cache[blockId];
		
		if (materialType == null) {
			return null;
		}
		
		CraftCustomMaterial material = materialType[metaData];
		
		if (material == null) {
			material = new CraftCustomMaterial(blockId, metaData, nameCache[blockId]);
			materialType[metaData] = material;
		}
		
		return material;
		
	}
	
	public static CraftCustomMaterial getMaterial(String name, int metaData) {
		
		Integer blockId = nameLookup.get(name);
		if (blockId == null) {
			return null;
		}
		return getMaterial(blockId, metaData);
		
	}
	
	public String getName() {
		return name;
	}
	
	public int getTypeId() {
		return blockId;
	}
	
	public int getData() {
		return metaData;
	}
	
}
