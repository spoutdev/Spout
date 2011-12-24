package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.Message;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.net.Session;

public abstract class MessageHandler<T extends Message> {
	public abstract void handle(Session session, SpoutPlayer player, T message);
}
