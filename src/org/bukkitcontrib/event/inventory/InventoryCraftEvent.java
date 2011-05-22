package org.bukkitcontrib.event.inventory;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkitcontrib.inventory.CraftingInventory;

public class InventoryCraftEvent extends InventoryEvent{
    private static final long serialVersionUID = 2252453296883258337L;
    private ItemStack result;
    private InventorySlotType slotType;
    private int slotNum;
    protected ItemStack[][] matrix;
    int width, height;

    public InventoryCraftEvent(Player player, CraftingInventory inventory, Location location, InventorySlotType slotType, int slot, ItemStack[][] recipe, ItemStack result) {
        super("InventoryCraftEvent", player, inventory, location);
        this.matrix = recipe;
        this.width = recipe.length;
        this.height = recipe[0].length;
        this.result = result;
        this.slotType = slotType;
        this.slotNum = slot;
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
     * Get's the current (new) item at the slot
     * @return current item
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Set's the current item at the slot
     * @param newItem to set
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
}
