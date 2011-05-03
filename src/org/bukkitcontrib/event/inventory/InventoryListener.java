package org.bukkitcontrib.event.inventory;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class InventoryListener extends CustomEventListener implements Listener{
    
    public InventoryListener() {}
    
    public void onInventoryClose(InventoryCloseEvent event) {

    }
    
    public void onInventoryOpen(InventoryOpenEvent event) {
    }
    
    public void onInventoryClick(InventoryClickEvent event) {

    }
    
    @Override
    public void onCustomEvent(Event event) {
        if (event instanceof InventoryCloseEvent) {
            onInventoryClose((InventoryCloseEvent)event);
        }
        else if (event instanceof InventoryOpenEvent) {
            onInventoryOpen((InventoryOpenEvent)event);
        }
        else if (event instanceof InventoryClickEvent) {
            onInventoryClick((InventoryClickEvent)event);
        }
    }

}
