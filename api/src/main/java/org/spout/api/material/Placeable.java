/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.material;

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.math.vector.Vector3;

/**
 * An interface defining a {@link Material} that can be placed
 */
public interface Placeable {
	/**
	 * Called when this block is about to be placed (before {@link #onPlacement(Block, short, BlockFace, boolean)}), checking if placement is allowed or not.
	 *
	 * @param block to place
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @param isClickedBlock whether the block is to be placed at the clicked block
	 * @param cause the cause of the placement
	 * @return true if placement is allowed
	 */
	public boolean canPlace(Block block, short data, BlockFace against, Vector3 clickedPos, boolean isClickedBlock, Cause<?> cause);

	/**
	 * Called when this block is placed, handling the actual placement<br> This method should only change properties that rely on the face it is placed against, or in what way it is placed. All other
	 * logic should be performed in onCreate.
	 *
	 * @param block to affect
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @param clickedPos relative position the block was clicked to place this block
	 * @param isClickedBlock whether the block is being placed at the clicked block
	 * @param cause the cause of the placement
	 */
	public void onPlacement(Block block, short data, BlockFace against, Vector3 clickedPos, boolean isClickedBlock, Cause<?> cause);
}
