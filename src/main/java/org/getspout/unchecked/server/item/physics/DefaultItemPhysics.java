package org.getspout.unchecked.server.item.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemProperties;

public class DefaultItemPhysics implements ItemPhysics {
	private final int id;

	public DefaultItemPhysics(int itemId) {
		id = itemId;
	}

	@Override
	public MaterialData getPlacedBlock(BlockFace against, int data) {
		MaterialData type = ItemProperties.get(id).getPlacedBlock();
		if (type == null) {
			type = NO_PLACE;
		}
		return type;
	}

	@Override
	public boolean interact(SpoutPlayer interactingPlayer, SpoutBlock clicked, SpoutItemStack heldItem, Action type, BlockFace against) {
		return true;
	}
}
