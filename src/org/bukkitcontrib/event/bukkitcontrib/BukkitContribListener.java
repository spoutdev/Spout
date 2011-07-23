package org.bukkitcontrib.event.bukkitcontrib;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class BukkitContribListener extends CustomEventListener implements Listener{

	public BukkitContribListener() {

	}
	
	public void onBukkitContribSPEnable(BukkitContribSPEnable event) {
 
	}
	
	public void onServerTick(ServerTickEvent event) {
		
	}

	@Override
	public void onCustomEvent(Event event) {
		if (event instanceof BukkitContribSPEnable) {
			onBukkitContribSPEnable((BukkitContribSPEnable)event);
		}
		else if (event instanceof ServerTickEvent) {
			onServerTick((ServerTickEvent)event);
		}
	}

}
