package org.getspout.api.generator;

import org.getspout.api.geo.cuboid.Chunk;

public interface Populator {
	
	/**
	 * Populates the chunk.  
	 * 
	 * This method may make full use of the block modifying methods of the API.
	 * 
	 * This method will be called once per chunk and it is guaranteed that a 2x2x2 cube of chunks containing the chunk will be loaded.
	 * 
	 * The chunk to populate is the chunk with the lowest x, y and z coordinates of the cube.
	 * 
	 * This allows the populator to create features that cross chunk boundaries.
	 * 
	 * @param c the chunk to populate
	 */
	public void populate(Chunk c);
}
