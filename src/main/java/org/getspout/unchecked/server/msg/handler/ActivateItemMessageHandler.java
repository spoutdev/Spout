package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.notch.msg.ActivateItemMessage;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class ActivateItemMessageHandler extends MessageHandler<ActivateItemMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, ActivateItemMessage message) {
		if (player == null) {
			return;
		}

		if (message.getSlot() < 0 || message.getSlot() > 8) {
			return;
		}

		player.getInventory().setHeldItemSlot(message.getSlot());
	}
}
