package org.getspout.unchecked.server.block.physics;

import org.bukkit.block.BlockFace;

import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.block.SpoutBlockState;
import org.getspout.unchecked.server.block.SpoutContainerBlock;
import org.getspout.unchecked.server.entity.SpoutPlayer;

public class ContainerPhysics extends DefaultBlockPhysics {

	@Override
	public boolean interact(SpoutPlayer player, SpoutBlock block, boolean rightClick, BlockFace against) {
		if (rightClick) {
			SpoutBlockState state = block.getState();
			if (state instanceof SpoutContainerBlock) {
				SpoutContainerBlock container = (SpoutContainerBlock) state;
				player.openWindow(container.getInventory());
				return false;
			}
		}
		return true;
	}
}
