package org.getspout.commons.material;

public interface Material {
	
	public int getRawId();
	
	public int getRawData();
	
	public boolean hasSubtypes();
	
	public String getName();
	
	public String getNotchianName();
	
	public void setName(String name);
}
