package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.Message;
import org.getspout.server.net.Session;

public abstract class MessageHandler<T extends Message> {
	public abstract void handle(Session session, SpoutPlayer player, T message);
}
