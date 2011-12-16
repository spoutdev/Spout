package org.getspout.server.item.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

import org.getspout.server.block.SpoutBlock;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.item.ItemProperties;

public class DefaultItemPhysics implements ItemPhysics {
	private final int id;

	public DefaultItemPhysics(int itemId) {
		this.id = itemId;
	}

	public MaterialData getPlacedBlock(BlockFace against, int data) {
		MaterialData type = ItemProperties.get(id).getPlacedBlock();
		if (type == null) type = NO_PLACE;
		return type;
	}

	public boolean interact(SpoutPlayer interactingPlayer, SpoutBlock clicked, SpoutItemStack heldItem, Action type, BlockFace against) {
		return true;
	}
}
