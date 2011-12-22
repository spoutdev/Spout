package org.getspout.server.net;

import java.util.logging.Level;

import org.getspout.server.SpoutServer;
import org.getspout.server.msg.Message;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import org.getspout.server.SpoutServer;
import org.getspout.server.msg.Message;

/**
 * A {@link SimpleChannelUpstreamHandler} which processes incoming network
 * events.
 *
 * @author Graham Edgecombe.
 */
public class MinecraftHandler extends SimpleChannelUpstreamHandler {
	/**
	 * The server.
	 */
	private final SpoutServer server;

	/**
	 * Creates a new network event handler.
	 *
	 * @param server The server.
	 */
	public MinecraftHandler(SpoutServer server) {
		this.server = server;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		server.getChannelGroup().add(c);

		Session session = new Session(server, c);
		server.getSessionRegistry().add(session);
		ctx.setAttachment(session);

		server.getLogger().info("Channel connected: " + c + ".");
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		server.getChannelGroup().remove(c);

		Session session = (Session) ctx.getAttachment();
		server.getSessionRegistry().remove(session);
		session.dispose(true);

		server.getLogger().info("Channel disconnected: " + c + ".");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Session session = (Session) ctx.getAttachment();
		session.messageReceived((Message) e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		Channel c = e.getChannel();
		if (c.isOpen()) {
			server.getChannelGroup().remove(c);

			Session session = (Session) ctx.getAttachment();
			server.getSessionRegistry().remove(session);
			session.dispose(true);

			server.getLogger().log(Level.WARNING, "Exception caught, closing channel: " + c + "...", e.getCause());
			c.close();
		}
	}
}
