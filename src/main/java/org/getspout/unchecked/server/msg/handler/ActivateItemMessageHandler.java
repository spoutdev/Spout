package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.ActivateItemMessage;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class ActivateItemMessageHandler extends MessageHandler<ActivateItemMessage> {
	@Override
	public void handle(Session session, Player player, ActivateItemMessage message) {
		if (player == null) {
			return;
		}

		if (message.getSlot() < 0 || message.getSlot() > 8) {
			return;
		}

		//player.getInventory().setHeldItemSlot(message.getSlot());
	}
}
