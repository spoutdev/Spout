package org.getspout.api.generator;

import java.util.Random;

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
	 * The random number generator will be seeded based on the world seed and the x, y and z coordinates of the chunk so that it gives consistent results.
	 * 
	 * @param c the chunk to populate
	 * @param rng a pre-seeded random number generator
	 */
	public void populate(Chunk c, Random rng);
}
