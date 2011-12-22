package org.getspout.unchecked.server.msg.handler;

import org.bukkit.event.server.ServerListPingEvent;
import org.getspout.unchecked.server.EventFactory;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.msg.KickMessage;
import org.getspout.unchecked.server.msg.ServerListPingMessage;
import org.getspout.unchecked.server.net.Session;

/**
 * Format: (MOTD)/u00A7(# online)/u00A7(Max Players) /u00A7(Protocol Version
 * (This is added in case someone finds it useful, since it's not used by the
 * vanilla client.))
 */
public class ServerListPingMessageHandler extends MessageHandler<ServerListPingMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, ServerListPingMessage message) {
		ServerListPingEvent event = EventFactory.onServerListPing(session.getAddress().getAddress(), session.getServer().getMotd(), session.getServer().getOnlinePlayers().length, session.getServer().getMaxPlayers());
		String text = event.getMotd() + "\u00A7" + event.getNumPlayers();
		text += "\u00A7" + event.getMaxPlayers() + "\u00A7" + SpoutServer.PROTOCOL_VERSION;
		session.send(new KickMessage(text));
	}
}
