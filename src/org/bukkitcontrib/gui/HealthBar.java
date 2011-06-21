package org.bukkitcontrib.gui;

import org.bukkitcontrib.player.ContribPlayer;

public class HealthBar extends GenericWidget{
	protected final ContribPlayer player;
	
	public HealthBar(ContribPlayer player) {
		this.player = player;
	}
	
	public void update() {
		//TODO send health packet
	}

}
