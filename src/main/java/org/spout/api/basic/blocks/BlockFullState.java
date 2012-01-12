package org.spout.api.basic.blocks;

public class BlockFullState<T> {

	public short id;
	public short data;
	public T auxData;
	
	public BlockFullState() {
	}
	
	public BlockFullState(short id, short data, T auxData) {
		this.id = id;
		this.data = data;
		this.auxData = auxData;
	}
	
}
