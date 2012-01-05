package org.getspout.api.generator;

import org.getspout.api.util.cuboid.CuboidShortBuffer;

public interface WorldGenerator {
	/**
	 * Gets the block structure for a Chunk.  
	 * 
	 * The CuboidBuffer will always be exactly one Chunk in size (16x16x16 blocks) and Chunk aligned.
	 * 
	 * Structural blocks should not contain any lighting sources and the generator should give repeatable results.
	 * 
	 * It is recommended that seeded random number generators from WorldGeneratorUtils are used.
	 * 
	 * @param blockData a zeroed CuboidBuffer corresponding to the Chunk
	 */
	public void generate(CuboidShortBuffer blockData);
	
	/**
	 * Gets an array of Populators for the world generator
	 * 
	 * @return the Populator array
	 */
	public Populator[] getPopulators();
}
