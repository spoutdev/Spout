package org.bukkitcontrib.event.bukkitcontrib;

import org.bukkit.event.Event;
import org.bukkitcontrib.player.ContribPlayer;

public class BukkitContribSPEnable extends Event{
	private ContribPlayer player;
	public BukkitContribSPEnable(ContribPlayer player) {
		super("BukkitContribSPEnable");
		this.player = player;
	}

	/**
	 * Returns the player who just had their BukkitContrib SinglePlayer Mod enabled
	 * @return player
	 */
	public ContribPlayer getPlayer() {
		return player;
	}
}
