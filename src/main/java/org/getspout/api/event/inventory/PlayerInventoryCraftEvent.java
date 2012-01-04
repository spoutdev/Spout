/*
 * This file is part of Spout API (http://wiki.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.inventory;

import org.getspout.api.event.HandlerList;
import org.getspout.api.inventory.CraftingInventory;
import org.getspout.api.inventory.ItemStack;
import org.getspout.api.player.Player;

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
