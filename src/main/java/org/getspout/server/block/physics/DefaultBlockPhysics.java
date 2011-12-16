package org.getspout.server.block.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

import org.getspout.server.block.SpoutBlock;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.entity.SpoutPlayer;

public class DefaultBlockPhysics implements BlockPhysicsHandler {

	@Override
	public boolean canPlaceAt(SpoutBlock loc, BlockFace against) {
		return true;
	}

	@Override
	public boolean doPhysics(SpoutBlock block) {
		return false;
	}

	@Override
	public boolean postUpdateNeighbor(SpoutBlock block, BlockFace against) {
		return false;
	}

	@Override
	public int getPlacedMetadata(SpoutPlayer placer, int current, BlockFace against) {
		return current;
	}

	@Override
	public SpoutBlockState placeAgainst(SpoutPlayer player, SpoutBlockState block, MaterialData data, BlockFace against) {
		block.setTypeId(data.getItemTypeId());
		data.setData((byte)getPlacedMetadata(player, data.getData(), against));
		block.setData(data);
		return block;
	}

	@Override
	public boolean interact(SpoutPlayer player, SpoutBlock block, boolean rightClick, BlockFace against) {
		return true;
	}
}
