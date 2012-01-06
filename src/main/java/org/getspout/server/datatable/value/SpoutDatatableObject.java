package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.api.datatable.DatatableTuple;
import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;


public class SpoutDatatableObject implements DatatableTuple {
	public static final byte Persist = 0x1;
	public static final byte Sync = 0x2;

	protected int keyID;
	protected byte flags;
	Object data;

	public SpoutDatatableObject(int key) {
		keyID = key;
	}

	public SpoutDatatableObject(int key, Object dat) {
		keyID = key;
		this.data = dat;
	}

	@Override
	public void set(int key, Object value) {
		keyID = key;
		if (!(value instanceof Vector3) || !(value instanceof Vector2) || !(value instanceof Quaternion))
			throw new IllegalArgumentException("Unsuported Metadata type");
		data = value;

	}

	@Override
	public int hashCode() {
		return keyID;
	}

	@Override
	public void setFlags(byte flags) {
		this.flags = flags;

	}

	@Override
	public void setPersistant(boolean value) {
		if (value) flags &= SpoutDatatableObject.Persist;
		else flags &= ~SpoutDatatableObject.Persist;
	}

	@Override
	public void setSynced(boolean value) {
		if (value) flags &= SpoutDatatableObject.Sync;
		else flags &= ~SpoutDatatableObject.Sync;
	}

	@Override
	public Object get() {
		return data;
	}

	@Override
	public int asInt() {
		throw new NumberFormatException("Cannot represent Object as int");
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("Cannot represent Object as float");
	}

	@Override
	public boolean asBool() {
		throw new NumberFormatException("Cannot represent Object as boolean");
	}

	@Override
	public void output(OutputStream out) throws IOException {

		
	}

	@Override
	public void input(InputStream in) throws IOException {
		
	}


}
