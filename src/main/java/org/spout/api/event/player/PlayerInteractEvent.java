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
package org.spout.api.event.player;

import org.spout.api.entity.Player;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.Result;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.block.BlockFace;

/**
 * Called when a player interacts with the game world, or an item.
 * Implements {@link Cancellable}.  If this is canceled the interaction will not happen.
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Point interactedPoint;
	private final ItemStack heldItem;
	private final Action action;
	private final boolean isAir;
	private final BlockFace clickedFace;
	private Result itemResult = Result.DEFAULT;
	private Result blockResult = Result.DEFAULT;

	public PlayerInteractEvent(Player p, Point interactedPoint, ItemStack heldItem, Action action, boolean isAir, BlockFace clickedFace) {
		super(p);
		this.interactedPoint = interactedPoint;
		this.heldItem = heldItem;
		this.action = action;
		this.isAir = isAir;
		this.clickedFace = clickedFace;
	}

	/**
	 * Item that the player is currently holding.
	 * @return item held.
	 */
	public ItemStack getHeldItem() {
		return heldItem;
	}

	/**
	 * Determines how the interaction will affect the block that is clicked
	 * 
	 * @return result
	 */
	public Result interactWithBlock() {
		return blockResult;
	}

	/**
	 * Sets how the interaction will affect the block that is clicked
	 * 
	 * @param result
	 */
	public void setInteractWithBlock(Result result) {
		this.blockResult = result;
	}

	/**
	 * Determines if the item in the hand is used
	 * 
	 * @return result
	 */
	public Result useItemInHand() {
		return itemResult;
	}

	/**
	 * Alters the result for the item in hand
	 * 
	 * @param result
	 */
	public void setUseItemInHand(Result result) {
		this.itemResult = result;
	}

	/**
	 * The location that the Player is interacting with.<br/>
	 * @return point the inteaction is happening at.
	 */
	public Point getInteractedPoint() {
		return interactedPoint;
	}

	/**
	 * The action the player is performing.
	 * @return action being performed.
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Checks if the block being interacted with is an air block.
	 * @return true, if the block being interacted with is air.
	 */
	public boolean isAir() {
		return isAir;
	}

	/**
	 * Returns the block face that was clicked, if a block was clicked,
	 * or BlockFace.THIS if no block face was clicked (open air).
	 * 
	 * @return block face clicked
	 */
	public BlockFace getClickedFace() {
		return clickedFace;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
		if (cancelled) {
			setUseItemInHand(Result.DENY);
			setInteractWithBlock(Result.DENY);
		}
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static enum Action {
		LEFT_CLICK,
		RIGHT_CLICK,
	}
}
