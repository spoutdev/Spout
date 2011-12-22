package org.getspout.server.block.physics;

import gnu.trove.set.hash.TIntHashSet;

import org.bukkit.block.BlockFace;
import org.getspout.server.block.SpoutBlock;

public class SpecialPlaceBelowPhysics extends DefaultBlockPhysics {
	private final TIntHashSet allowedGround;
	private final boolean whitelist;

	public SpecialPlaceBelowPhysics(int type, boolean whitelist, int... belowTypes) {
		allowedGround = new TIntHashSet(belowTypes);
		this.whitelist = whitelist;
	}

	@Override
	public boolean canPlaceAt(SpoutBlock block, BlockFace against) {
		int below = block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ());
		return whitelist ? allowedGround.contains(below) : !allowedGround.contains(below);
	}
}
