package org.getspout.api.generator;

import java.util.Random;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;

public class WorldGeneratorUtils {
	
	/**
	 * Seeds a Random for a particular chunk.
	 * 
	 * This gives consistent results for world generation.
	 * 
	 * @param rng the Random
	 * @param c the chunk
	 */
	public void seedRandom(Random rng, Chunk c) {
		seedRandom(rng, c.getWorld(), c.getX(), c.getY(), c.getZ());
	}
		
	/**
	 * Seeds a Random for a particular chunk.
	 * 
	 * This gives consistent results for world generation.
	 * 
	 * @param rng the Random
	 * @param world the World containing the Chunk
	 * @param x the x coordinate for the Chunk (blockX << 4)
	 * @param y the y coordinate for the Chunk (blockY << 4)
	 * @param z the z coordinate for the Chunk (blockZ << 4)
	 */
	public void seedRandom(Random rng, World world, int x, int y, int z) {
		long hash = world.getSeed();
		hash += (hash << 5) + x;
		hash += (hash << 5) + y;
		hash += (hash << 5) + z;
		
		rng.setSeed(hash);
	}

}
