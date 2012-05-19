/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.util.map.concurrent;

import org.spout.api.material.block.BlockFullState;
import org.spout.api.util.StringMap;

public class MappedBlockStore<T> extends AtomicBlockStore<T> {
	private final StringMap map;
	public MappedBlockStore(int shift, StringMap map) {
		super(shift, 10);
		this.map = map;
	}
	
	public MappedBlockStore(int shift, short[] initial, StringMap map) {
		super(shift, 10, initial);
		this.map = map;
	}
	
	public MappedBlockStore(int shift, int dirtySize, StringMap map) {
		super(shift, dirtySize, null);
		this.map = map;
	}

	public MappedBlockStore(int shift, int dirtySize, short[] initial, StringMap map) {
		super(shift, dirtySize, initial, null);
		this.map = map;
	}
	
	public MappedBlockStore(int shift, int dirtySize, short[] blocks, short[] data, StringMap map) {
		super(shift, dirtySize, blocks, data, null);
		this.map = map;
	}
	
	public MappedBlockStore(int shift, int dirtySize, short[] blocks, short[] data, T[] auxData, StringMap map) {
		super(shift, dirtySize, blocks, data, auxData);
		this.map = map;
	}
	
	@Override
	public int getBlockId(int x, int y, int z) {
		return mapId(super.getBlockId(x, y, z));
	}
	
	@Override
	public BlockFullState getFullData(int x, int y, int z) {
		BlockFullState state = super.getFullData(x, y, z);
		state.setId((short)mapId(state.getId()));
		return state;
	}
	
	@Override
	public BlockFullState getFullData(int x, int y, int z, BlockFullState input) {
		BlockFullState state = super.getFullData(x, y, z, input);
		state.setId((short)mapId(state.getId()));
		return state;
	}
	
	@Override
	public void setBlock(int x, int y, int z, short id, short data) {
		super.setBlock(x, y, z, (short)mapId(id), data);
	}
	
	@Override
	public boolean compareAndSetBlock(int x, int y, int z, short expectId, short expectData, short newId, short newData) {
		return super.compareAndSetBlock(x, y, z, (short)mapId(expectId), expectData, (short)mapId(newId), newData);
	}
	
	@Override
	public short[] getBlockIdArray() {
		short[] ids = super.getBlockIdArray();
		for (int i = 0; i < ids.length; i++) {
			ids[i] = (short)mapId(ids[i]);
		}
		return ids;
	}
	
	@Override
	public short[] getBlockIdArray(short[] array) {
		short[] ids = super.getBlockIdArray(array);
		for (int i = 0; i < ids.length; i++) {
			ids[i] = (short)mapId(ids[i]);
		}
		return ids;
	}
	
	private int mapId(int id) {
		int mappedId = map.convertToParent(id);
		return mappedId != 0 ? mappedId : id;
	}

}
