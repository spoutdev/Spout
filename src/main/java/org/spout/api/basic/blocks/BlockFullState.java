package org.spout.api.basic.blocks;

public class BlockFullState<T> {

	private short id;
	private short data;
	private T auxData;
	
	public BlockFullState() {
	}
	
	public BlockFullState(short id, short data, T auxData) {
		this.id = id;
		this.data = data;
		this.auxData = auxData;
	}
	
	public final short getId() {
		return id;
	}
	
	public final void setId(short id) {
		this.id = id;
	}
	
	public final short getData() {
		return data;
	}
	
	public final void setData(short data) {
		this.data = data;
	}
	
	public final T getAuxData() {
		return auxData;
	}
	
	public final void setAuxData(T auxData) {
		this.auxData = auxData;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "{" + id + ", " + data + ", " + auxData + "}";
	}
	
}
