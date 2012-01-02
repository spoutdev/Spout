package org.getspout.api.datatable;

public interface DatatableTuple extends Outputable {

	public void set(String key, Object value);
	
	public void setFlags(byte flags);
	
	public void setPersistant(boolean value);
	
	public void setSynced(boolean value);
	
	public Object get();
	
	public int asInt();
	
	public float asFloat();
	
	public boolean asBool();
	
	
}
