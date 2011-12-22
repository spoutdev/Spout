package org.getspout.server.block.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.getspout.server.block.BlockID;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.entity.SpoutPlayer;

public class DoubleStepPhysics extends DefaultBlockPhysics {
	@Override
	public SpoutBlockState placeAgainst(SpoutPlayer player, SpoutBlockState block, MaterialData data, BlockFace against) {
		if (against == BlockFace.UP && data.getItemTypeId() == BlockID.STEP) {
			SpoutBlockState possibleStair = block.getBlock().getRelative(against.getOppositeFace()).getState();
			if (possibleStair.getTypeId() == BlockID.STEP && possibleStair.getRawData() == data.getData()) {
				possibleStair.setTypeId(BlockID.DOUBLE_STEP);
				possibleStair.setData(new MaterialData(BlockID.DOUBLE_STEP, data.getData()));
				return possibleStair;
			}
		}
		return super.placeAgainst(player, block, data, against);
	}
}
