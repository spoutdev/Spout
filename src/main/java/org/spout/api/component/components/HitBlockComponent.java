/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.component.components;

import org.spout.api.entity.Player;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.util.BlockIterator;

/**
 * A component allowing a player to interact with a block
 */
public class HitBlockComponent extends EntityComponent {
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
	public Block getTargetBlock() {
		BlockIterator blockIt = getAlignedBlocks();
		Block block = blockIt.getTarget();
		if (blockIt.getBlockFace()!=null) {
			System.out.println("Face hit: " + blockIt.getBlockFace());
		}
		return block;
	}
	
	/**
	 * Return a list of all the blocks in line
	 * of view.
	 * @return blocks
	 */
	public BlockIterator getAlignedBlocks() {
		return new BlockIterator(player.getWorld(), player.getTransform().getTransform(), range);
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
