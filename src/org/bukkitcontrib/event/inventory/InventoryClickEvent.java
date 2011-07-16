package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkitcontrib.inventory.ContribCraftInventoryPlayer;

public class InventoryClickEvent extends InventoryEvent{
	private static final long serialVersionUID = -5555208587016292520L;
	protected InventorySlotType type;
	protected ItemStack item;
	protected ItemStack cursor;
	protected int slot;
	protected int convertedSlot;
	protected Result result = Result.DEFAULT;
	protected boolean leftClick;
	protected boolean shift;

	public InventoryClickEvent(Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot, boolean leftClick, boolean shift) {
		super("InventoryClickEvent", player, inventory);
		this.type = type;
		this.item = item;
		this.cursor = cursor;
		this.slot = slot;
		this.convertedSlot = convertSlot(this.slot);
		this.leftClick = leftClick;
		this.shift = shift;
	}
	
	public InventoryClickEvent(Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot, boolean leftClick, boolean shift, Location location) {
		super("InventoryClickEvent", player, inventory, location);
		this.type = type;
		this.item = item;
		this.cursor = cursor;
		this.slot = slot;
		this.convertedSlot = convertSlot(this.slot);
		this.leftClick = leftClick;
		this.shift = shift;
	}
	
	protected InventoryClickEvent(String name, Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot, boolean leftClick, boolean shift, Location location) {
		super(name, player, inventory, location);
		this.type = type;
		this.item = item;
		this.cursor = cursor;
		this.slot = slot;
		this.convertedSlot = convertSlot(this.slot);
		this.leftClick = leftClick;
		this.shift = shift;
	}
	
	@Override
	public void setCancelled(boolean cancel){
		if (cancel) this.result = Result.DENY;
		super.setCancelled(cancel);
	}
	
	/**
	 * Get's the result of this event.
	 * Default: Allow for Minecraft to handle the inventory click normally
	 * Allow: Allow the inventory click to continue, regardless of the consequences
	 * Deny: Block the inventory click from occuring, reset the inventory state to the pre-click state
	 * @return result
	 */
	public Result getResult() {
		return this.result;
	}
	
	/**
	 * Set's the result of this event.
	 * Default: Allow for Minecraft to handle the inventory click normally
	 * Allow: Allow the inventory click to continue, regardless of the consequences
	 * Deny: Block the inventory click from occuring, reset the inventory state to the pre-click state
	 */
	public void setResult(Result result) {
		this.result = result;
		if (result == Result.DENY) {
			setCancelled(true);
		}
	}
	
	/**
	 * Get's the type of slot that is being interacted with
	 * @return slot type
	 */
	public InventorySlotType getSlotType() {
		return this.type;
	}
	
	/**
	 * Get's the item at the slow being interacted with, or null if empty
	 * @return item
	 */
	public ItemStack getItem() {
		return this.item;
	}
	
	/**
	 * Set's the slot being interacted with. Use null for an empty slot.
	 * Note: The inventory slot can not be changed unless the result has been set to Allow.
	 * @param item to set
	 */
	public void setItem(ItemStack item) {
		if (this.result != Result.ALLOW){
			throw new UnsupportedOperationException("Can not alter stack contents without allowing any result");
		}
		this.item = item;
	}
	
	/**
	 * Get's the cursor being interacted with, or null if empty.
	 * @return cursor
	 */
	public ItemStack getCursor() {
		return this.cursor;
	}
	
	/**
	 * Set's the cursor being interacted with. Use null for an empty slot.
	 * Note: The cursor can not be changed unless the result has been set to Allow.
	 * @param cursor to set
	 */
	public void setCursor(ItemStack cursor) {
		if (this.result != Result.ALLOW){
			throw new UnsupportedOperationException("Can not alter cursor stack contents without allowing any result");
		}
		this.cursor = cursor;
	}
	
	/**
	 * Get's the slot index being interacted with
	 * If the slot is -999, the clicked region is outside of the inventory
	 * @return slot index
	 */
	public int getSlot() {
		return this.convertedSlot;
	}
	
	/**
	 * Get's the raw slot index that the packet sent
	 * If the slot is -999, the clicked region is outside of the inventory
	 * @return raw slot
	 */
	public int getRawSlot() {
		return this.slot;
	}
	
	/**
	 * Return's true if the click on the inventory window was a left click. If false, it was a right click.
	 * @return true if left click
	 */
	public boolean isLeftClick() {
		return leftClick;
	}
	
	/**
	 * Return's true if the click on the inventory crafting slow was a shift click. 
	 * @return true if shift click
	 */
	public boolean isShiftClick() {
		return shift;
	}
	
	protected int convertSlot(int slot) {
		if(getInventory() instanceof ContribCraftInventoryPlayer) {
			int size = getInventory().getSize();
			//Armour slot
			switch(slot) {
			case 5: return 39;
			case 6: return 38;
			case 7: return 37;
			case 8: return 36;
			}
			//Quickslots
			if (slot > size) {
				slot -= size;
			}

			return slot;
		}
		return slot;
	}

}
