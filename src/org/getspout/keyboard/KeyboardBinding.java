package org.getspout.keyboard;

import org.getspout.player.ContribPlayer;

public interface KeyboardBinding {
	
	public void onPreKeyPress(ContribPlayer player);
	
	public void onPostKeyPress(ContribPlayer player);
	
	public void onPreKeyRelease(ContribPlayer player);
	
	public void onPostKeyRelease(ContribPlayer player);
}
