package org.bukkitcontrib.player;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public interface ContribPlayer extends org.bukkit.entity.Player{
    
    /**
     * Closes any dialog windows the client may have open at the time
     * @return true if a window was closed
     */
    public boolean closeActiveWindow();
    
    /**
     * Opens an inventory dialog to the player, with the given inventory displayed in the upper pane, and the player's inventory in the lower pane
     * @param inventory to use in the dialog GUI
     * @return true if an inventory window was opened
     */
    public boolean openInventoryWindow(Inventory inventory);
    
    /**
     * Opens an inventory dialog to the player, with the given inventory displayed in the upper pane, and the player's inventory in the lower pane.
     * The location is not used, but is passed to other plugins when notifying them of the open window
     * @param inventory to use in the dialog GUI
     * @param location that represents this inventory in the world (e.g Chest, Furnace). Use null if there is no physical location.
     * @return true if an inventory window was opened
     */
    public boolean openInventoryWindow(Inventory inventory, Location location);
    
    /**
     * Opens an workbench dialog to the player, using the workbench at the given location
     * @param location of the workbench to use. Must be a valid workbench.
     * @return true if a workbench window was opened
     */
    public boolean openWorkbenchWindow(Location location);

}
