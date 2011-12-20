package org.getspout.server.block.physics;

import org.bukkit.block.BlockFace;

import org.getspout.server.block.SpoutBlock;
import org.getspout.server.block.data.ToggleableAttachable;
import org.getspout.server.entity.SpoutPlayer;

public class ToggleableAttachablePhysics extends AttachablePhysics {
	protected final ToggleableAttachable data;

	public ToggleableAttachablePhysics(ToggleableAttachable data) {
		super(data);
		this.data = data;
	}

	@Override
	public boolean interact(SpoutPlayer player, SpoutBlock block, boolean rightClick, BlockFace against) {
		block.setData((byte) data.toggleOpen(block.getData()));
		return false;
	}
}
