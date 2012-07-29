package org.spout.api.protocol.builder;

public class FieldRef<T> {
	
	private final int[] index;
	
	public FieldRef() {
		this(new int[0]);
	}
	
	public FieldRef(int index) {
		this(new int[] {index});
	}
	
	public FieldRef(int[] index) {
		int length = index.length;
		this.index = new int[length];
		System.arraycopy(index, 0, this.index, 0, length);
	}

	public int[] getIndex() {
		return index;
	}
	
}
