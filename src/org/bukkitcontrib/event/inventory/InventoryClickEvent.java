package org.bukkitcontrib.event.inventory;

import org.bukitcontrib.inventory.ContribCraftInventoryPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickEvent extends InventoryEvent{
    private static final long serialVersionUID = -5555208587016292520L;
    protected InventorySlotType type;
    protected ItemStack item;
    protected ItemStack cursor;
    protected int slot;
    protected int convertedSlot;
    protected Result result = Result.DEFAULT;

    public InventoryClickEvent(Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot) {
        super("InventoryClickEvent", player, inventory);
        this.type = type;
        this.item = item;
        this.cursor = cursor;
        this.slot = slot;
        this.convertedSlot = convertSlot(this.slot);
    }
    
    public InventoryClickEvent(Player player, Inventory inventory, InventorySlotType type, ItemStack item, ItemStack cursor, int slot, Location location) {
        super("InventoryClickEvent", player, inventory, location);
        this.type = type;
        this.item = item;
        this.cursor = cursor;
        this.slot = slot;
        this.convertedSlot = convertSlot(this.slot);
    }
    
    @Override
    public void setCancelled(boolean cancel){
        if (cancel) this.result = Result.DENY;
        super.setCancelled(cancel);
    }
    
    public Result getResult() {
        return this.result;
    }
    
    public void setResult(Result result) {
        this.result = result;
    }
    
    public InventorySlotType getSlotType() {
        return this.type;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public ItemStack getCursor() {
        return this.cursor;
    }
    
    public void setCursor(ItemStack cursor) {
        this.cursor = cursor;
    }
    
    public int getSlot() {
        return this.convertedSlot;
    }
    
    public int getRawSlot() {
        return this.slot;
    }
    
    private int convertSlot(int slot) {
        if(getInventory() instanceof ContribCraftInventoryPlayer) {
            int size = getInventory().getSize();
            //Armour slot
            switch(slot) {
            case 5: return 39;
            case 6: return 38;
            case 7: return 37;
            case 8: return 36;
            }
            size += 4;
            slot -= size;
            if(slot >= 27) // Quickbar
                slot -= 27;
            else slot += 9;
            return slot;
        }
        return slot;
    }

}
