package org.bukkitcontrib.keyboard;

import org.bukkitcontrib.player.ContribPlayer;

public interface KeyboardBinding {
    
    public void onPreKeyPress(ContribPlayer player);
    
    public void onPostKeyPress(ContribPlayer player);
    
    public void onPreKeyRelease(ContribPlayer player);
    
    public void onPostKeyRelease(ContribPlayer player);
}
