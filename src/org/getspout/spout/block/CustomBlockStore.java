package org.getspout.spout.block;

import java.io.Serializable;

import gnu.trove.map.hash.TIntObjectHashMap;


public class CustomBlockStore implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int arraySize = 16*128*16;

	private final int arrayConvertThreshold = 128;
	private final int mapConvertThreshold = 64;

	private TIntObjectHashMap<CustomMaterial> customBlockMap = new TIntObjectHashMap<CustomMaterial>();
	private CustomMaterial[] customBlockArray = null;
	private boolean map = true;
	private int size = 0;

	public CustomMaterial setBlock(CustomMaterial material, int x, int y, int z) {

		if (material == null) {
			return removeBlock(x, y, z);
		}

		int key = getKey(x, y, z);

		CustomMaterial old;
		if (map) {
			old = (CustomMaterial)customBlockMap.put(key, material);
		} else {
			old = customBlockArray[key];
			customBlockArray[key] = material;
		}

		if (old == null) {
			size++;
			if (map && size > arrayConvertThreshold) {
				convertMapToArray();
			}
		}
		return old;
	}

	public CustomMaterial removeBlock(int x, int y, int z) {

		int key = getKey(x, y, z);

		CustomMaterial old;
		if (map) {
			old = (CustomMaterial)customBlockMap.remove(key);
		} else {
			old = customBlockArray[key];
			customBlockArray[key] = null;
		}

		if (old != null) {
			size--;
			if (!map && size < mapConvertThreshold) {
				convertArrayToMap();
			}
		}
		return old;

	}

	private void convertMapToArray() {

		int[] keys = customBlockMap.keys();
		customBlockArray = new CustomMaterial[arraySize];

		for (int key : keys) {
			customBlockArray[key] = (CustomMaterial)customBlockMap.get(key);
		}
		customBlockMap = null;
		map = false;
	}

	private void convertArrayToMap() {

		customBlockMap = new TIntObjectHashMap<CustomMaterial>();

		for (int i = 0; i < arraySize; i++) {
			CustomMaterial material = customBlockArray[i];
			if (material != null) {
				customBlockMap.put(i, material);
			}
		}

		customBlockArray = null;
		map = true;
	}

	private int getKey(int x, int y, int z) {
		return ((x & 0xF) << 4) | ((y & 0xFF) << 8) | ((z & 0xF) << 0);
	}

}
