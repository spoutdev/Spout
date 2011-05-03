package org.bukkitcontrib.player;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public interface ContribPlayer extends org.bukkit.entity.Player{
    
    public boolean closeActiveWindow();
    
    public boolean openInventoryWindow(Inventory inventory);
    
    public boolean openWorkbenchWindow(Location location);

}
