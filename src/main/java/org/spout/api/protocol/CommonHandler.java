/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.protocol;

import java.util.logging.Level;

import org.spout.api.Server;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * A {@link SimpleChannelUpstreamHandler} which processes incoming network
 * events.
 *
 * @author Graham Edgecombe.
 */
public class CommonHandler extends SimpleChannelUpstreamHandler {
	/**
	 * The server.
	 */
	private final Server server;
	
	/**
	 * The associated session
	 */
	private volatile Session session = null;

	/**
	 * Creates a new network event handler.
	 *
	 * @param server The server.
	 */
	public CommonHandler(Server server) {
		this.server = server;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		server.getChannelGroup().add(c);

		Session session = server.newSession(c);
		server.getSessionRegistry().add(session);
		ctx.setAttachment(session);
		this.session = session;

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
	
	public void setProtocol(Protocol protocol) {
		if (session != null) {
			session.setProtocol(protocol);
		} else {
			throw new IllegalStateException("The protocol cannot be set before the channel is associated with a session");
		}
	}
	
}
