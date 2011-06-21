package org.bukkitcontrib.gui;

import org.bukkitcontrib.player.ContribPlayer;

public class ChatBar extends GenericWidget implements Widget{
	protected final ContribPlayer player;
	public ChatBar(ContribPlayer player) {
		this.player = player;
	}

}
