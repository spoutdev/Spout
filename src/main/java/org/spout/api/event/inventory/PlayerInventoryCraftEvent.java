/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.inventory;

import org.spout.api.event.HandlerList;
import org.spout.api.inventory.CraftingInventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.player.Player;

/**
 * Called when a player crafts something.
 */
public class PlayerInventoryCraftEvent extends PlayerInventoryEvent {
	private static HandlerList handlers = new HandlerList();

	private ItemStack result;

	private ItemStack cursor;

	private int slotNum;

	private ItemStack[][] matrix;

	private int width, height;

	private boolean left;

	private boolean shift;

	public PlayerInventoryCraftEvent(Player p) {
		super(p);
	}

	/**
	 * Gets the inventory where the crafting is taking place
	 *
	 * @return inventory
	 */

	@Override
	public CraftingInventory getInventory() {
		return (CraftingInventory) inventory;
	}

	/**
	 * Gets the width of the inventory crafting area
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets the height of the inventory crafting area
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the recipe at the inventory crafting area
	 *
	 * @return recipe
	 */
	public ItemStack[][] getRecipe() {
		return matrix;
	}

	public void setRecipe(ItemStack[][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * Gets the itemstack at the cursor
	 *
	 * @return cursor
	 */
	public ItemStack getCursor() {
		return cursor;
	}

	/**
	 * Sets the itemstack at the cursor
	 *
	 * @param cursor to set
	 */
	public void setCursor(ItemStack cursor) {
		this.cursor = cursor;
	}

	/**
	 * Gets the current (new) item at the slot
	 *
	 * @return current item
	 */
	public ItemStack getResult() {
		return result;
	}

	/**
	 * Sets the current item at the slot
	 *
	 * @param result to set
	 */
	public void setResult(ItemStack result) {
		this.result = result;
	}

	/**
	 * Gets the slot index being interacted with
	 *
	 * @return slot index
	 */
	public int getSlot() {
		return slotNum;
	}

	public void setSlot(int slotNum) {
		this.slotNum = slotNum;
	}

	/**
	 * Returns true if the click on the inventory crafting slot was a left
	 * click. If false, it was a right click.
	 *
	 * @return true if left click
	 */
	public boolean isLeftClick() {
		return left;
	}

	public void setLeftClick(boolean left) {
		this.left = left;
	}

	/**
	 * Returns true if the click on the inventory crafting slot was a shift
	 * click.
	 *
	 * @return true if shift click
	 */
	public boolean isShiftClick() {
		return shift;
	}

	public void setShiftClick(boolean shift) {
		this.shift = shift;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
