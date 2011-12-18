/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.player;

import org.getspout.api.block.Block;
import org.getspout.api.block.BlockFace;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.inventory.ItemStack;

/**
 * Called when a bucket is used.
 */
public class PlayerBucketEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private ItemStack bucket;

	private Block blockClicked;

	private BlockFace blockFace;

	private boolean filled = false;

	/**
	 * Get the resulting bucket in hand after the bucket event
	 *
	 * @return Bucket held in hand after the event.
	 */
	public ItemStack getBucket() {
		return bucket;
	}

	/**
	 * Set the item in hand after the event
	 *
	 * @param bucket the new bucket after the bucket event.
	 */
	public void setBucket(ItemStack bucket) {
		this.bucket = bucket;
	}

	/**
	 * Return the block clicked
	 *
	 * @return the clicked block
	 */
	public Block getBlockClicked() {
		return blockClicked;
	}

	public void setBlockClicked(Block blockClicked) {
		this.blockClicked = blockClicked;
	}

	/**
	 * Get the face on the clicked block
	 *
	 * @return the clicked face
	 */
	public BlockFace getBlockFace() {
		return blockFace;
	}

	public void setBlockFace(BlockFace blockFace) {
		this.blockFace = blockFace;
	}

	/**
	 * Returns true if the bucket was filled.
	 * 
	 * @return True if the bucket was filled.
	 */
	public boolean isFilled() {
		return filled;
	}

	/**
	 * Returns true if the bucket was emptied.
	 * 
	 * @return True if the bucket was emptied.
	 */
	public boolean isEmptied() {
		return !filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
