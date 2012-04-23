package org.spout.api.datatable.value;

import java.io.Serializable;

public class DatatableSerializable extends DatatableObject {

	public DatatableSerializable(int key) {
		super(key);
	}
	
	public DatatableSerializable(int key, Serializable data) {
		super(key, data);
	}

	@Override
	public byte[] compress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decompress(byte[] compressed) {
		// TODO Auto-generated method stub
	}

}
