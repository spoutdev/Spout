package org.getspout.unchecked.server.item.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemID;

public class FilledBucketPhysics extends DefaultItemPhysics {
	private final int placedFluidId;
	private final boolean fillCauldron;

	public FilledBucketPhysics(int itemId, int placedFluidId, boolean fillCauldron) {
		super(itemId);
		this.placedFluidId = placedFluidId;
		this.fillCauldron = fillCauldron;
	}

	@Override
	public boolean interact(SpoutPlayer player, SpoutBlock block, SpoutItemStack heldItem, Action action, BlockFace against) {
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return true;
		}
		SpoutBlock target = block.getRelative(against);
		if (fillCauldron && block.getTypeId() == BlockID.CAULDRON) {
			if (block.getData() < 3) {
				block.setData((byte) 3);
				heldItem.setTypeId(ItemID.BUCKET);
			}
			return false;
		} else if (target.isEmpty()) {
			target.setTypeId(placedFluidId);
			heldItem.setTypeId(ItemID.BUCKET);
			return false;
		}
		return super.interact(player, block, heldItem, action, against);
	}
}
