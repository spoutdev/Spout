package org.getspout.api.geo.cuboid;

/**
 * Represents a Cuboid that can be moved in discrete steps based on its size
 * 
 * The size of the Cuboid may not be altererd
 */
public interface MovableCuboid {
	
	/**
	 * Sets the x integer coordinate of the Cuboid
	 * 
	 * @param x the integer coordinate
	 */
	public void setX(int x);

	/**
	 * Sets the y integer coordinate of the Cuboid
	 * 
	 * @param y the integer coordinate
	 */
	public void setY(int y);
	
	/**
	 * Sets the z integer coordinate of the Cuboid
	 * 
	 * @param z the integer coordinate
	 */
	public void setZ(int z);
}
