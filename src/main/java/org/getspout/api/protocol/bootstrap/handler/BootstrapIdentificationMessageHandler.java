package org.getspout.api.protocol.bootstrap.handler;

import org.getspout.api.Commons;
import org.getspout.api.event.Event;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.bootstrap.msg.BootstrapIdentificationMessage;

public class BootstrapIdentificationMessageHandler extends MessageHandler<BootstrapIdentificationMessage> {
	@Override
	public void handle(Session session, Player player, BootstrapIdentificationMessage message) {
		if (Commons.isSpout) {
			Event event = new PlayerConnectEvent(session, message.getName());
			session.getGame().getEventManager().callEvent(event);
		}
	}
}
