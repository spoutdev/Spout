package org.bukkitcontrib.event.screen;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class ScreenListener extends CustomEventListener implements Listener{
	
	public void onScreenOpenEvent(ScreenOpenEvent event) {
		
	}
	
	public void onScreenCloseEvent(ScreenCloseEvent event) {
		
	}
	
	@Override
	public void onCustomEvent(Event event) {
		if (event instanceof ScreenOpenEvent) {
			onScreenOpenEvent((ScreenOpenEvent)event);
		}
		else if (event instanceof ScreenCloseEvent) {
			onScreenCloseEvent((ScreenCloseEvent)event);
		}
	}

}
