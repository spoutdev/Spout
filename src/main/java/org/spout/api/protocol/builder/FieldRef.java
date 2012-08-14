package org.spout.api.protocol.builder;

public class FieldRef<T> {
	
	private final int index;
	
	public FieldRef(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
}
