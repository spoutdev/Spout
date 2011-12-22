package org.getspout.server.generator.populators.trees;

import java.util.Random;

import org.bukkit.World;

public interface GenericTreeGenerator {

    public boolean generate(Random random, int centerX, int centerZ, int height, World world);
}
