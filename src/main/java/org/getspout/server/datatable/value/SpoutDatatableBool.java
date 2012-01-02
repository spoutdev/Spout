package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.server.datatable.SpoutDatatableProto.DatatableEntry;
import org.getspout.server.datatable.SpoutDatatableProto.DatatableValue;

public class SpoutDatatableBool extends SpoutDatatableObject {

	boolean data;
	
	@Override
	public void set(String key, Object value) {
		throw new IllegalArgumentException("This is an boolean value, use set(string,bool)");

	}

	public void set(String key, boolean value) {
		keyhash = key.hashCode();
		data= value;
	}

	@Override
	public Object get() {
		throw new NumberFormatException("this value cannot be expressed as an object");
	}

	@Override
	public int asInt() {
		return (data)?1:0;
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("this value cannot be expressed as an float");
	}

	@Override
	public boolean asBool() {
		return data;
	}

	@Override
	public void output(OutputStream out) throws IOException {
		DatatableValue value = DatatableValue.newBuilder().setBoolval(data).build();
		DatatableEntry entry = DatatableEntry.newBuilder().setKeyHash(keyhash).setFlags(flags).setValue(value).build();
		entry.writeTo(out);
		
	}

	@Override
	public void input(InputStream in) throws IOException {
		DatatableEntry entry = DatatableEntry.parseFrom(in);
		keyhash = entry.getKeyHash();
		flags = (byte)entry.getFlags();
		data = entry.getValue().getBoolval();
		
	}
}
