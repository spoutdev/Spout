package org.spout.api.datatable;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

public class StringData extends AbstractData {

	private final AtomicReference<String> data = new AtomicReference<String>("");

	public StringData(int key) {
		super(key);
	}

	public StringData(int key, String value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Object value) {
		if (value instanceof String) {
			set((String)value);
			return;
		}
		throw new IllegalArgumentException("This is an String value, use set(String)");
	}

	public void set(String value) {
		data.set(value);
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public byte[] compress() {
		return data.get().getBytes();
	}

	@Override
	public void decompress(byte[] compressed) {
		set(new String(compressed));
	}

	@Override
	public byte getObjectTypeId() {
		return 3;
	}

	@Override
	public AbstractData newInstance(int key) {
		return new LongData(key);
	}

	@Override
	public int fixedLength() {
		return -1;
	}
}