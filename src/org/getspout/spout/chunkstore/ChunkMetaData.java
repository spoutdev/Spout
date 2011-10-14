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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.util.BlockVector;
import org.getspout.spout.chunkstore.Utils.DummyClass;

public class ChunkMetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	// This data is saved. This means data can handle different map heights
	// Changes may be needed to the positionToKey method

	private int cx;
	private int cz;
	private UUID worldUid;

	private int[] blockX = new int[1];
	private int[] blockY = new int[1];
	private int[] blockZ = new int[1];

	private HashMap<String, byte[]>[] blockData = null;
	private HashMap<String, byte[]> chunkData = null;
	@SuppressWarnings("unchecked")
	private HashMap<String, Serializable>[] blockDataAsObject = new HashMap[1];
	private HashMap<String, Serializable> chunkDataAsObject = new HashMap<String, Serializable>();
	private transient HashMap<String, Serializable>[] blockDataAsObjectTransient;
	private transient HashMap<String, Serializable> chunkDataAsObjectTransient;

	private int blocks = 0;

	// This is just a fast lookup for the data
	transient private HashMap<Integer, HashMap<String, Serializable>> fastLookup = null;
	transient private HashMap<Integer, Integer> indexLookup = null;

	transient private boolean dirty = false;

	ChunkMetaData(UUID worldId, int cx, int cz) {
		this.cx = cx;
		this.cz = cz;
		this.worldUid = worldId;
		refreshLookup();
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
		if (serial != null && serial instanceof DummyClass) {
			try {
				serial = Utils.deserializeRaw(((DummyClass) serial).serialData);
				chunkDataAsObject.put(id, serial);
			} catch (ClassNotFoundException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		}
		return serial;
	}

	public Serializable putChunkData(String id, Serializable o) {

		Serializable serial = chunkDataAsObject.put(id, o);

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

		HashMap<String, Serializable> localBlockData = fastLookup.get(key);

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

		HashMap<String, Serializable> localBlockData = fastLookup.get(key);

		if (localBlockData == null) {
			return null;
		} else {
			Serializable serial = localBlockData.get(id);
			if (serial != null && serial instanceof DummyClass) {
				try {
					serial = Utils.deserializeRaw(((DummyClass) serial).serialData);
					localBlockData.put(id, serial);
				} catch (ClassNotFoundException e) {
					return null;
				} catch (IOException e) {
					return null;
				}
			}
			return serial;
		}

	}

	public Serializable putBlockData(String id, int x, int y, int z, Serializable o) {

		int key = positionToKey(x, y, z);

		HashMap<String, Serializable> localBlockData = fastLookup.get(key);

		if (localBlockData == null) {
			if (blocks >= this.blockDataAsObject.length) {
				resizeArrays(1 + ((blocks * 3) / 2));
			}
			localBlockData = new HashMap<String, Serializable>();
			blockX[blocks] = x;
			blockY[blocks] = y;
			blockZ[blocks] = z;
			blockDataAsObject[blocks] = localBlockData;
			fastLookup.put(key, blockDataAsObject[blocks]);
			indexLookup.put(key, blocks);
			blocks++;
			refreshLookup();
		}

		Serializable oldObject = localBlockData.put(id, o);
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

		HashMap<String, Serializable>[] oldBlockData = blockDataAsObject;

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

	public void refreshLookup() {
		fastLookup = new HashMap<Integer, HashMap<String, Serializable>>();
		indexLookup = new HashMap<Integer, Integer>();
		for (int i = 0; i < blocks; i++) {
			int key = positionToKey(blockX[i], blockY[i], blockZ[i]);
			fastLookup.put(key, blockDataAsObject[i]);
			indexLookup.put(key, i);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean updateFromVersionOne() {
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
						Map.Entry<String, byte[]> entry = itr.next();
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
					Map.Entry<String, byte[]> entry = itr.next();
					chunkDataAsObject.put(entry.getKey(), Utils.deserialize(entry.getValue()));
				}
			}
			chunkData = null;
			return true;
		} else {
			return false;
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		blockDataAsObjectTransient = blockDataAsObject;
		blockDataAsObject = null;
		chunkDataAsObjectTransient = chunkDataAsObject;
		chunkDataAsObject = null;
		out.defaultWriteObject();
		int length = blockX.length;
		out.writeInt(length);
		for (int i = 0; i < length; i++) {
			writeMap(out, blockDataAsObjectTransient[i]);
		}
		writeMap(out, chunkDataAsObjectTransient);
		blockDataAsObject = blockDataAsObjectTransient;
		chunkDataAsObject = chunkDataAsObjectTransient;
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		updateFromVersionOne();
		int length;
		try {
			length = in.readInt();
		} catch (EOFException eof) {
			System.out.println("[Spout] Info: No extra chunk meta data for chunk " + cx + ", " + cz);
			System.out.println("[Spout] Info: On next save, chunk will be saved in new format");
			dirty = true;
			refreshLookup();
			return;
		}
		blockDataAsObject = new HashMap[length];
		for (int i = 0; i < length; i++) {
			blockDataAsObject[i] = readMap(in);
		}
		refreshLookup();
	}

	private void writeMap(ObjectOutputStream out, HashMap<String, Serializable> map) throws IOException {

		if (map == null) {
			out.writeBoolean(false);
			return;
		} else {
			out.writeBoolean(true);
		}
		
		out.writeObject(map);

	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Serializable> readMap(ObjectInputStream in) throws IOException {

		if (!in.readBoolean()) {
			return null;
		}

		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		
		try {
			map = (HashMap<String, Serializable>) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;

	}

}
