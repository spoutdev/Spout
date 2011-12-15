package org.getspout.server.msg.handler;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.PingMessage;
import org.getspout.server.net.Session;

public class PingMessageHandler extends MessageHandler<PingMessage> {

    @Override
    public void handle(Session session, SpoutPlayer player, PingMessage message) {
        if (session.getPingMessageId() == message.getPingId()) {
            session.pong();
        }
    }
    
}
