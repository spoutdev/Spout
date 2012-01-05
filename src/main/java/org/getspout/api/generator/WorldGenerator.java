package org.getspout.api.generator;

import java.util.Random;

import org.getspout.api.util.cuboid.CuboidShortBuffer;

public interface WorldGenerator {
	/**
	 * Gets the block structure for a Chunk.  
	 * 
	 * The CuboidBuffer will always be exactly one Chunk in size (16x16x16 blocks) and Chunk aligned.
	 * 
	 * The random number generator will be seeded based on the world seed and the x, y and z coordinates of the chunk so that it gives consistent results.
	 * 
	 * Structural blocks should not contain any lighting sources and the generator should give repeatable results.
	 * 
	 * @param blockData a zeroed CuboidBuffer corresponding to the Chunk
	 * @param rng a pre-seeded random number generator
	 */
	public void generate(CuboidShortBuffer blockData, Random rng);
	
	/**
	 * Gets an array of Populators for the world generator
	 * 
	 * @return the Populator array
	 */
	public Populator[] getPopulators();
}
