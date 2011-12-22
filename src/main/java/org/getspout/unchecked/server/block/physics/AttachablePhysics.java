package org.getspout.unchecked.server.block.physics;

import org.bukkit.block.BlockFace;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.block.data.Attachable;
import org.getspout.unchecked.server.entity.SpoutPlayer;

public class AttachablePhysics extends DefaultBlockPhysics {
	protected final Attachable data;

	public AttachablePhysics(Attachable data) {
		this.data = data;
	}

	@Override
	public boolean canPlaceAt(SpoutBlock block, BlockFace against) {
		switch (data.getPlaceRequirement()) {
			case ATTACHED_BLOCK_SIDE:
				if (against == BlockFace.SELF || against == BlockFace.DOWN || against == BlockFace.UP) {
					return false;
				}
				return block.getRelative(against.getOppositeFace()).getTypeId() != BlockID.AIR;
			case BLOCK_BELOW:
				return block.getWorld().getBlockTypeIdAt(block.getX(), block.getY(), block.getZ()) != 0;
			case ANYWHERE:
			default:
				return true;
		}
	}

	@Override
	public int getPlacedMetadata(SpoutPlayer placer, int current, BlockFace against) {
		switch (data.getAttachmentType()) {
			case PLAYER_DIRECTION:
				return data.setAttachedFace(current, placer.getFacingDirection());
			case PLAYER_CARDINAL_DIRECTION:
				return data.setAttachedFace(current, placer.getCardinalDirection());
			case CLICKED_BLOCK:
			default:
				return data.setAttachedFace(current, against);
		}
	}
}
