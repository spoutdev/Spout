package org.getspout.server.block.physics;

import org.bukkit.block.BlockFace;
import org.getspout.server.EventFactory;
import org.getspout.server.block.BlockProperties;
import org.getspout.server.block.SpoutBlock;

public class BlockPhysicsEngine {
	public static void doPhysics(SpoutBlock block) {
		if (!EventFactory.onBlockPhysics(block).isCancelled()) {
			int original = block.getTypeId();
			BlockPhysicsHandler handler = BlockProperties.get(block.getTypeId()).getPhysics();
			if (handler.doPhysics(block)) {
				for (BlockFace face : BlockFace.values()) {
			if (face != BlockFace.SELF) {
				neighborPhysics(block.getRelative(face), face, original);
			}
				}
			}
		}
	}

	public static void updateAllNeighbors(SpoutBlock block) {
		for (BlockFace face : BlockFace.values()) {
			if (face != BlockFace.SELF) {
				neighborPhysics(block.getRelative(face), face, block.getTypeId());
			}
		}
	}

	private static void neighborPhysics(SpoutBlock block, BlockFace against, int original) {
		if (!EventFactory.onBlockPhysics(block, original).isCancelled()) {
			BlockPhysicsHandler handler = BlockProperties.get(block.getTypeId()).getPhysics();
			if (handler.postUpdateNeighbor(block, against)) {
				original = block.getTypeId();
				for (BlockFace face : BlockFace.values()) {
					if (face != BlockFace.SELF && face != against) {
						neighborPhysics(block.getRelative(face), face, original);
					}
				}
			}
		}
	}
}
