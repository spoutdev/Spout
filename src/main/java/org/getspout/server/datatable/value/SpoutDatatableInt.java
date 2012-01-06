package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SpoutDatatableInt extends SpoutDatatableObject {
	int data;

	public SpoutDatatableInt(int key) {
		super(key);
	}

	public SpoutDatatableInt(int key, int value) {
		super(key);
		this.data = value;
	}
	
	@Override
	public void set(int key, Object value) {
		throw new IllegalArgumentException("This is an int value, use set(string,int)");

	}

	public void set(String key, int value) {
		keyID = key.hashCode();
		data = value;
	}

	@Override
	public Object get() {
		throw new NumberFormatException("this value cannot be expressed as an object");
	}

	@Override
	public int asInt() {
		return data;
	}

	@Override
	public float asFloat() {
		return data;
	}

	@Override
	public boolean asBool() {
		return (data != 0);
	}

	@Override
	public void output(OutputStream out) throws IOException {
	
		
	}

	@Override
	public void input(InputStream in) throws IOException {
	

	}

}
