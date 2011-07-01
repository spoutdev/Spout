package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkitcontrib.inventory.CraftingInventory;

public class InventoryCraftEvent extends InventoryEvent{
    private static final long serialVersionUID = 2252453296883258337L;
    private ItemStack result, cursor;
    private InventorySlotType slotType;
    private int slotNum;
    private ItemStack[][] matrix;
    private int width, height;
    private boolean left;
    private boolean shift;

    public InventoryCraftEvent(Player player, CraftingInventory inventory, Location location, InventorySlotType slotType, int slot, ItemStack[][] recipe, ItemStack result, ItemStack cursor, boolean leftClick, boolean shift) {
        super("InventoryCraftEvent", player, inventory, location);
        this.matrix = recipe;
        this.width = recipe.length;
        this.height = recipe[0].length;
        this.result = result;
        this.slotType = slotType;
        this.slotNum = slot;
        this.cursor = cursor;
        this.left = leftClick;
        this.shift = shift;
    }

    /**
     * Get's the inventory where the crafting is taking place
     */
    public CraftingInventory getInventory() {
        return (CraftingInventory)this.inventory;
    }

    /**
     * Get's the height of the inventory crafting area
     * @return height
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get's the width of the inventory crafting area
     * @return width
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get's the recipe at the inventory crafting area
     * @return width
     */
    public ItemStack[][] getRecipe() {
        return matrix;
    }
    
    /**
     * Get's the itemstack at the cursor
     * @return
     */
    public ItemStack getCursor() {
    	return cursor;
    }
    
    /**
     * Set's the itemstack at the cursor
     * @param cursor to set
     */
    public void setCursor(ItemStack cursor) {
    	this.cursor = cursor;
    }

    /**
     * Get's the current (new) item at the slot
     * @return current item
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Set's the current item at the slot
     * @param result to set
     */
    public void setResult(ItemStack result) {
        this.result = result;
    }

    /**
     * Get's the slot index being interacted with
     * @return slot index
     */
    public int getSlot() {
        return slotNum;
    }

    /**
     * Get's the slot type being interacted with
     * @return slot type
     */
    public InventorySlotType getSlotType() {
        return slotType;
    }
    
    /**
     * Return's true if the click on the inventory crafting slot was a left click. If false, it was a right click.
     * @return true if left click
     */
    public boolean isLeftClick() {
        return left;
    }
    
    /**
     * Return's true if the click on the inventory crafting slow was a shift click. 
     * @return true if shift click
     */
    public boolean isShiftClick() {
    	return shift;
    }
}
