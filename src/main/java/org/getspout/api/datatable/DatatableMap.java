package org.getspout.api.datatable;

public interface DatatableMap extends Outputable {

	public void set(String key, DatatableTuple value);
	
	public DatatableTuple get(String key);
	
	public byte[] compress();
	
	public void decompress(byte[] compressedData);
	
}
