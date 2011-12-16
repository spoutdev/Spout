package org.getspout.server.item.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

import org.getspout.server.block.BlockID;
import org.getspout.server.item.ItemID;

public class SignItemPhysics extends DefaultItemPhysics {
	public SignItemPhysics() {
		super(ItemID.SIGN);
	}

	public MaterialData getPlacedBlock(BlockFace against, int data) {
		if (against == BlockFace.UP)  {
			return new MaterialData(BlockID.SIGN_POST);
		} else {
			return new MaterialData(BlockID.WALL_SIGN);
		}
	}
}
