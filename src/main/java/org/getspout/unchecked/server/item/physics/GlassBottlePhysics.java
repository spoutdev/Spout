package org.getspout.unchecked.server.item.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemID;

public class GlassBottlePhysics extends DefaultItemPhysics {
	public GlassBottlePhysics() {
		super(ItemID.GLASS_BOTTLE);
	}

	@Override
	public boolean interact(SpoutPlayer player, SpoutBlock block, SpoutItemStack heldItem, Action action, BlockFace against) {
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return true;
		}
		if (block.getTypeId() == BlockID.CAULDRON && block.getData() > 0) {
			block.setData((byte) (block.getData() - 1));
			heldItem.setTypeId(ItemID.POTION);
			return false;
		}
		return super.interact(player, block, heldItem, action, against);
	}
}
