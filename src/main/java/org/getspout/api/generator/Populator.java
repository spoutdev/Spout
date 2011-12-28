package org.getspout.api.generator;

import java.util.Random;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;

public interface Populator {
	public void Populate(Chunk c, World w, Random rng);
}
