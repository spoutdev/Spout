package org.bukkitcontrib.gui;

import org.bukkitcontrib.player.ContribPlayer;

public class BubbleBar extends GenericWidget implements Widget{
	protected final ContribPlayer player;
	public BubbleBar(ContribPlayer player) {
		this.player = player;
	}
	
	public void update() {
		//TODO send update packet?
	}
}
