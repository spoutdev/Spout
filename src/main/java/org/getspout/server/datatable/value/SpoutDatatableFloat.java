package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SpoutDatatableFloat extends SpoutDatatableObject {
	float data;

	public SpoutDatatableFloat(int key) {
		super(key);
	}

	public SpoutDatatableFloat(int key, float value) {
		super(key);
		this.data = value;
	}

	@Override
	public void set(int key, Object value) {
		throw new IllegalArgumentException("This is an int value, use set(string,float)");

	}

	public void set(String key, float value) {
		keyID = key.hashCode();
		data = value;
	}

	@Override
	public Object get() {
		throw new NumberFormatException("this value cannot be expressed as an object");
	}

	@Override
	public int asInt() {
		return (int)data;
	}

	@Override
	public float asFloat() {
		return data;
	}

	@Override
	public boolean asBool() {
		throw new NumberFormatException("this value cannot be expressed as an boolean");
	}

	@Override
	public void output(OutputStream out) throws IOException {
		
	}

	@Override
	public void input(InputStream in) throws IOException {
	

	}

}
