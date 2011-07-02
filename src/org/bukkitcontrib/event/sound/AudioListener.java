package org.bukkitcontrib.event.sound;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class AudioListener extends CustomEventListener implements Listener{
    
    public AudioListener() {
        
    }
    
    public void onBackgroundMusicChange(BackgroundMusicEvent event) {
        
    }
    
    public void onCustomEvent(Event event) {
        if (event instanceof BackgroundMusicEvent) {
            onBackgroundMusicChange((BackgroundMusicEvent)event);
        }
    }
}
