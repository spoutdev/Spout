package org.getspout.server.generator.populators.trees;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import org.getspout.server.block.BlockID;

public class NormalTree implements GenericTreeGenerator {
    @Override
    public boolean generate(Random random, int centerX, int centerZ, int height, World world) {
        ArrayList<BlockState> allBlocks = new ArrayList<BlockState>();
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Block sourceBlock = world.getBlockAt(centerX, centerY, centerZ);

        // TODO check for nearby trees

       if (sourceBlock.getTypeId() != BlockID.GRASS) {
           return false;
       }

        BlockState dirtState = sourceBlock.getState();
        dirtState.setTypeId(BlockID.DIRT);
        allBlocks.add(dirtState);

        ArrayList<BlockState> leaves = new ArrayList<BlockState>();
        leaves.add(world.getBlockAt(centerX, centerY + height + 1, centerZ).getState());

        for (int j = 0; j < 4; j++) {
            leaves.add(world.getBlockAt(centerX, centerY + height + 1 - j, centerZ - 1).getState());
            leaves.add(world.getBlockAt(centerX, centerY + height + 1 - j, centerZ - 1).getState());
            leaves.add(world.getBlockAt(centerX, centerY + height + 1 - j, centerZ + 1).getState());
            leaves.add(world.getBlockAt(centerX - 1, centerY + height + 1 - j, centerZ).getState());
            leaves.add(world.getBlockAt(centerX + 1, centerY + height + 1 - j, centerZ).getState());
        }
        if (random.nextBoolean()) {
            leaves.add(world.getBlockAt(centerX + 1, centerY + height, centerZ + 1).getState());
        }
        if (random.nextBoolean()) {
            leaves.add(world.getBlockAt(centerX + 1, centerY + height, centerZ - 1).getState());
        }
        if (random.nextBoolean()) {
            leaves.add(world.getBlockAt(centerX - 1, centerY + height, centerZ + 1).getState());
        }
        if (random.nextBoolean()) {
            leaves.add(world.getBlockAt(centerX - 1, centerY + height, centerZ - 1).getState());
        }
        leaves.add(world.getBlockAt(centerX + 1, centerY + height - 1, centerZ + 1).getState());
        leaves.add(world.getBlockAt(centerX + 1, centerY + height - 1, centerZ - 1).getState());
        leaves.add(world.getBlockAt(centerX - 1, centerY + height - 1, centerZ + 1).getState());
        leaves.add(world.getBlockAt(centerX - 1, centerY + height - 1, centerZ - 1).getState());
        leaves.add(world.getBlockAt(centerX + 1, centerY + height - 2, centerZ + 1).getState());
        leaves.add(world.getBlockAt(centerX + 1, centerY + height - 2, centerZ - 1).getState());
        leaves.add(world.getBlockAt(centerX - 1, centerY + height - 2, centerZ + 1).getState());
        leaves.add(world.getBlockAt(centerX - 1, centerY + height - 2, centerZ - 1).getState());
        for (int j = 0; j < 2; j++) {
            for (int k = -2; k <= 2; k++) {
                for (int l = -2; l <= 2; l++) {
                    leaves.add(world.getBlockAt(centerX + k, centerY + height - 1 - j, centerZ + l).getState());
                }
            }
        }
        for (BlockState block : leaves) {
            block.setTypeId(BlockID.LEAVES);
        }
        allBlocks.addAll(leaves);

        ArrayList<BlockState> air = new ArrayList<BlockState>();
        for (int j = 0; j < 2; j++) {
            if (random.nextBoolean()) {
                air.add(world.getBlockAt(centerX + 2, centerY + height - 1 - j, centerZ + 2).getState());
            }
            if (random.nextBoolean()) {
                air.add(world.getBlockAt(centerX + 2, centerY + height - 1 - j, centerZ - 2).getState());
            }
            if (random.nextBoolean()) {
                air.add(world.getBlockAt(centerX - 2, centerY + height - 1 - j, centerZ + 2).getState());
            }
            if (random.nextBoolean()) {
                air.add(world.getBlockAt(centerX - 2, centerY + height - 1 - j, centerZ - 2).getState());
            }
        }
        for (BlockState block : air) {
            block.setTypeId(BlockID.AIR);
        }
        allBlocks.addAll(air);

        ArrayList<BlockState> log = new ArrayList<BlockState>();
        for (int y = 1; y <= height; y++) {
            log.add(world.getBlockAt(centerX, centerY + y, centerZ).getState());
        }
        for (BlockState block : log) {
            block.setTypeId(BlockID.LOG);
        }
        allBlocks.addAll(log);
        // TODO event
        for (BlockState block : allBlocks){
			block.update(true);
        }
        return true;
    }
}
