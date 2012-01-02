package org.getspout.api.datatable;

public interface DatatableTuple extends Outputable {

	public void set(String key, Outputable value);
	
	public void setFlags(byte flags);
	
	public void setPersistant(boolean value);
	
	public void setSynced(boolean value);
	
	public Outputable get();
	
	public int asInt();
	
	public float asFloat();
	
	public boolean asBool();
	
}
