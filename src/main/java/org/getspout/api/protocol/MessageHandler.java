package org.getspout.api.protocol;

import org.getspout.api.player.Player;


public abstract class MessageHandler<T extends Message> {
	public abstract void handle(Session session, Player player, T message);
}
