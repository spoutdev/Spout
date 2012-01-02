package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.server.datatable.SpoutDatatableProto.DatatableEntry;
import org.getspout.server.datatable.SpoutDatatableProto.DatatableValue;

public class SpoutDatatableFloat extends SpoutDatatableObject {

	float data;
	
	@Override
	public void set(String key, Object value) {
		throw new IllegalArgumentException("This is an int value, use set(string,float)");

	}

	public void set(String key, float value) {
		keyhash = key.hashCode();
		data= value;
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
		DatatableValue value = DatatableValue.newBuilder().setFloatval(data).build();
		DatatableEntry entry = DatatableEntry.newBuilder().setKeyHash(keyhash).setFlags(flags).setValue(value).build();
		entry.writeTo(out);
		
	}

	@Override
	public void input(InputStream in) throws IOException {
		DatatableEntry entry = DatatableEntry.parseFrom(in);
		keyhash = entry.getKeyHash();
		flags = (byte)entry.getFlags();
		data = entry.getValue().getFloatval();
		
	}

}
