package org.getspout.server.msg.handler;

import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

import org.getspout.server.EventFactory;
import org.getspout.server.block.BlockID;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.AnimateEntityMessage;
import org.getspout.server.net.Session;

/**
 * A {@link MessageHandler} which handles {@link org.bukkit.entity.Entity}
 * animation messages.
 */
public final class AnimateEntityMessageHandler extends MessageHandler<AnimateEntityMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, AnimateEntityMessage message) {
		Block block = player.getTargetBlock(null, 6);
		if (block == null || block.getTypeId() == BlockID.AIR) {
			if (EventFactory.onPlayerInteract(player, Action.LEFT_CLICK_AIR).isCancelled()) {
				return; // TODO: Item interactions
			}
		}
		if (EventFactory.onPlayerAnimate(player).isCancelled()) {
			return;
		}
		switch (message.getAnimation()) {
			case AnimateEntityMessage.ANIMATION_SWING_ARM:
				AnimateEntityMessage toSend = new AnimateEntityMessage(player.getEntityId(), AnimateEntityMessage.ANIMATION_SWING_ARM);
				for (SpoutPlayer observer : player.getWorld().getRawPlayers()) {
					if (observer != player && observer.canSee(player)) {
						observer.getSession().send(toSend);
					}
				}
				break;
			default:
				// TODO: other things?
				return;
		}
	}
}
