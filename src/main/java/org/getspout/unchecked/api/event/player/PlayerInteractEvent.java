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
package org.getspout.unchecked.api.event.player;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;
import org.getspout.unchecked.api.event.Result;
import org.getspout.unchecked.api.event.block.BlockAction;

/**
 * Called when a player interacts with an object or air.
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	protected BlockAction action;

	protected Block blockClicked;

	protected BlockFace blockFace;

	private Result useInteractedBlock;

	private Result useItemInHand;

	/**
	 * Returns the action type
	 *
	 * @return Action returns the type of interaction
	 */
	public BlockAction getAction() {
		return action;
	}

	public void setAction(BlockAction action) {
		this.action = action;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		setUseInteractedBlock(cancelled ? Result.DENY : useInteractedBlock().equals(Result.DENY) ? Result.DEFAULT : useInteractedBlock());
		setUseItemInHand(cancelled ? Result.DENY : useItemInHand().equals(Result.DENY) ? Result.DEFAULT : useItemInHand());
		super.setCancelled(cancelled);
	}

	/**
	 * Check if this event involved a block
	 *
	 * @return boolean true if it did
	 */
	public boolean hasBlock() {
		return blockClicked != null;
	}

	/**
	 * Returns the clicked block
	 *
	 * @return Block returns the block clicked with this item.
	 */
	public Block getClickedBlock() {
		return blockClicked;
	}

	public void setClickedBlock(Block block) {
		blockClicked = block;
	}

	/**
	 * Returns the face of the block that was clicked
	 *
	 * @return BlockFace returns the face of the block that was clicked
	 */
	public BlockFace getBlockFace() {
		return blockFace;
	}

	public void setBlockFace(BlockFace blockFace) {
		this.blockFace = blockFace;
	}

	/**
	 * This controls the action to take with the block (if any) that was clicked
	 * on This event gets processed for all blocks, but most don't have a
	 * default action
	 *
	 * @return the action to take with the interacted block
	 */
	public Result useInteractedBlock() {
		return useInteractedBlock;
	}

	/**
	 * @param useInteractedBlock the action to take with the interacted block
	 */
	public void setUseInteractedBlock(Result useInteractedBlock) {
		this.useInteractedBlock = useInteractedBlock;
	}

	/**
	 * This controls the action to take with the item the player is holding This
	 * includes both blocks and items (such as flint and steel or records) When
	 * this is set to default, it will be allowed if no action is taken on the
	 * interacted block
	 *
	 * @return the action to take with the item in hand
	 */
	public Result useItemInHand() {
		return useItemInHand;
	}

	/**
	 * @param useItemInHand the action to take with the item in hand
	 */
	public void setUseItemInHand(Result useItemInHand) {
		this.useItemInHand = useItemInHand;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
