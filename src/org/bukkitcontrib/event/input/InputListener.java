package org.bukkitcontrib.event.input;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class InputListener extends CustomEventListener implements Listener{
    
    public InputListener() {

    }
    
    public void onKeyPressedEvent(KeyPressedEvent event) {
	
    }
    
    public void onKeyReleasedEvent(KeyReleasedEvent event) {

    }
    
    @Deprecated
    public void onMouseClickedEvent(Event event) {

    }
    
    @Override
    public void onCustomEvent(Event event) {
        if (event instanceof KeyPressedEvent) {
            onKeyPressedEvent((KeyPressedEvent)event);
        }
        else if (event instanceof KeyReleasedEvent) {
            onKeyReleasedEvent((KeyReleasedEvent)event);
        }
    }
}
