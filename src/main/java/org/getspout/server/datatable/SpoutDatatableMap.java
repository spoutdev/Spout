package org.getspout.server.datatable;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.api.datatable.DatatableMap;
import org.getspout.api.datatable.DatatableTuple;
import org.getspout.api.util.StringMap;
import org.getspout.server.datatable.value.SpoutDatatableObject;

public class SpoutDatatableMap implements DatatableMap {
	final StringMap stringmap;
	TSynchronizedIntObjectMap<DatatableTuple> map = new TSynchronizedIntObjectMap<DatatableTuple>(new TIntObjectHashMap<DatatableTuple>());

	public SpoutDatatableMap(StringMap stringmap) {
		this.stringmap = stringmap;
	}

	@Override
	public void set(DatatableTuple value) {
		map.put(value.hashCode(), value);

	}

	public int getKey(String key) {
		return stringmap.register(key);
	}

	@Override
	public DatatableTuple get(String key) {
		return map.get(key.hashCode());
	}

	@Override
	public byte[] compress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decompress(byte[] compressedData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void output(OutputStream out) throws IOException {
		

	}

	@Override
	public void input(InputStream in) throws IOException {
		
	}

}
