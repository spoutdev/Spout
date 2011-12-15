/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.inventory;

import org.getspout.api.block.Block;
import org.getspout.api.block.BlockFace;

public interface DoubleChestInventory extends Inventory{
	/**
	 * Gets the block containing the top half of the double chest
	 * @return top half
	 */
	public Block getTopHalf();
	
	/**
	 * Gets the block containing the bottom half of the double chest
	 * @return bottom half
	 */
	public Block getBottomHalf();
	
	/**
	 * Gets the left half of the double chest
	 * @return left side
	 */
	public Block getLeftSide();
	
	/**
	 * Gets the right half of the double chest
	 * @return right side
	 */
	public Block getRightSide();

	/**
	 * Gets the direction of the front buckle on the double chest
	 * @return buckle direction
	 */
	public BlockFace getDirection();
}
