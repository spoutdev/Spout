/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.component.impl;

import org.spout.api.component.type.EntityComponent;
import org.spout.api.entity.Player;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.api.util.BlockIterator;

/**
 * A component allowing a player to interact with a block
 */
public class InteractComponent extends EntityComponent {
	private Player player;
	private float range = 8f;

	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new IllegalStateException("May only attach this component to players!");
		}
		player = (Player) getOwner();
	}

	/**
	 * Return the block in front of you eyes if there
	 * is one in range.
	 * @return block
	 */
	public Block getTargetBlock(boolean invisible) {
		BlockIterator blockIt = getAlignedBlocks();
		Block block = blockIt.getTarget(invisible);
		return block;
	}

	public Block getTargetBlock() {
		return getTargetBlock(false);
	}

	public BlockFace getTargetFace(boolean invisible) {
		BlockIterator blockIt = getAlignedBlocks();
		blockIt.getTarget(invisible);
		return blockIt.getBlockFace();
	}

	public BlockFace getTargetFace() {
		return getTargetFace(false);
	}

	/**
	 * Return the last block before an obstacle
	 * If there is no obstacle return null
	 * @return block
	 */
	public Block getLastEmpty() {
		BlockIterator blockIt = getAlignedBlocks();
		Block block = blockIt.getTarget();
		if (block == null) {
			return null;
		}
		return block.translate(blockIt.getBlockFace());
	}

	/**
	 * Return a list of all the blocks in line
	 * of view.
	 * @return blocks
	 */
	public BlockIterator getAlignedBlocks() {
		return new BlockIterator(player.getWorld(), player.getScene().getTransform(), range);
	}

	/**
	 * The max distance value you want the targeted block to be.
	 * @param range
	 */
	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
