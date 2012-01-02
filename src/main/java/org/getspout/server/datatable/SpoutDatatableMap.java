package org.getspout.server.datatable;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.api.datatable.DatatableMap;
import org.getspout.api.datatable.DatatableTuple;
import org.getspout.server.datatable.value.SpoutDatatableObject;

public class SpoutDatatableMap implements DatatableMap {
	TIntObjectHashMap<DatatableTuple> map = new TIntObjectHashMap<DatatableTuple>();
	@Override
	public void set(DatatableTuple value) {
		map.put(value.hashCode(), value);

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
		
		for(DatatableTuple dat : map.values()){
			dat.output(out);
		}
		
	}

	@Override
	public void input(InputStream in) throws IOException {
		DatatableTuple t = SpoutDatatableObject.read(in);
		while(t != null){
			map.put(t.hashCode(), t);
			t = SpoutDatatableObject.read(in);
		}
		
	}

}
