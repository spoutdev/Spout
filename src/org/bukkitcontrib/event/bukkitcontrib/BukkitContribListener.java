package org.bukkitcontrib.event.bukkitcontrib;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkitcontrib.event.input.KeyPressedEvent;

public class BukkitContribListener extends CustomEventListener implements Listener{

    public BukkitContribListener() {

    }
    
    public void onBukkitContribSPEnable(BukkitContribSPEnable event) {
 
    }
    
    public void onServerTick(ServerTickEvent event) {
        
    }

    @Override
    public void onCustomEvent(Event event) {
        if (event instanceof KeyPressedEvent) {
            onBukkitContribSPEnable((BukkitContribSPEnable)event);
        }
        else if (event instanceof ServerTickEvent) {
            onServerTick((ServerTickEvent)event);
        }
    }

}
