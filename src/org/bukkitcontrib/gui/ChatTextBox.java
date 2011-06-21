package org.bukkitcontrib.gui;

import org.bukkitcontrib.player.ContribPlayer;

public class ChatTextBox extends GenericWidget implements Widget{
	protected final ContribPlayer player;
	public ChatTextBox(ContribPlayer player) {
		this.player = player;
	}

}
