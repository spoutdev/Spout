package org.bukkitcontrib.event.inventory;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class InventoryListener extends CustomEventListener implements Listener{
    
    public InventoryListener() {}
    
    public void onInventoryClose(InventoryCloseEvent event) {
        event.setCancelled(true);
    }
    
    @Override
    public void onCustomEvent(Event event) {
        if (event instanceof InventoryCloseEvent) {
            onInventoryClose((InventoryCloseEvent)event);
        }
    }

}
