package org.getspout.api.datatable;

import java.io.Externalizable;

/**
 * Indicates that the implementing object has a Datatable associated with it
 * 
 *
 */
public interface Datatable {
	public void setData(String key, int value);
	
	public void setData(String key, float value);
	
	public void setData(String key, boolean value);
	
	public void setData(String key, Externalizable value);
	
	public DatatableTuple getData(String key);
	
}
