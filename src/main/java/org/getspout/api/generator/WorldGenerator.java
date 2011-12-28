package org.getspout.api.generator;

import java.util.Random;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;

public interface WorldGenerator {
	public Block[][] generate(World w, Random rng, int x, int z);
}
