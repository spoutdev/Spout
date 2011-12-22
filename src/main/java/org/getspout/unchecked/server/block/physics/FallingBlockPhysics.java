package org.getspout.unchecked.server.block.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingSand;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.block.SpoutBlock;

public class FallingBlockPhysics extends DefaultBlockPhysics {
	public FallingBlockPhysics(int id) {
	}

	@Override
	public boolean doPhysics(SpoutBlock block) {
		return checkBelowFree(block);
	}

	@Override
	public boolean postUpdateNeighbor(SpoutBlock block, BlockFace against) {
		return checkBelowFree(block);
	}

	public boolean checkBelowFree(SpoutBlock block) {
		if (block.getRelative(BlockFace.DOWN).getTypeId() == BlockID.AIR) {
			block.setTypeId(0);
			block.getWorld().spawn(block.getLocation(), FallingSand.class);
			return true;
		}
		return false;
	}
}
