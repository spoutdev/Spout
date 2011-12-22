package org.getspout.unchecked.server.msg.handler;

import org.bukkit.GameMode;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.msg.CloseWindowMessage;
import org.getspout.unchecked.server.net.Session;

public final class CloseWindowMessageHandler extends MessageHandler<CloseWindowMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, CloseWindowMessage message) {
		if (player == null) {
			return;
		}

		if (player.getItemOnCursor() != null) {
			// player.getWorld().dropItem(player.getEyeLocation(), player.getItemInHand());
			if (player.getGameMode() != GameMode.CREATIVE) {
				player.getInventory().addItem(player.getItemOnCursor());
			}
			player.setItemOnCursor(null);
		}

		player.onClosedWindow();
	}
}
