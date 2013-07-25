/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.protocol;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import org.spout.api.Engine;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;

/**
 * A {@link SimpleChannelUpstreamHandler} which processes incoming network events.
 */
public class CommonHandler extends SimpleChannelUpstreamHandler {
	/**
	 * The server.
	 */
	private final Engine engine;
	/**
	 * The associated session
	 */
	private AtomicReference<Session> session = new AtomicReference<Session>(null);
	/**
	 * Indicates if it is an upstream channel pipeline
	 */
	private final boolean onClient;
	private final CommonDecoder decoder;
	private final CommonEncoder encoder;

	/**
	 * Creates a new network event handler.
	 *
	 * @param engine The engine.
	 * @param upstream If the connections are going to the server
	 */
	public CommonHandler(Engine engine, CommonEncoder encoder, CommonDecoder decoder) {
		this.engine = engine;
		if (Spout.getPlatform() == Platform.CLIENT) {
			this.onClient = true;
		} else {
			this.onClient = false;
		}
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();

		// ctx.getPipeline().addBefore("2", "messagePrinter", new MessagePrintingHandler());

		if (onClient) {
			// Client
			engine.getLogger().info("Upstream channel connected: " + c + ".");
		} else {
			// Server
			try {
				Server server = (Server) engine;
				server.getChannelGroup().add(c);
				Session session = engine.newSession(c);
				server.getSessionRegistry().add(session);
				setSession(session);
				ctx.setAttachment(session);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("Exception thrown when connecting", ex);
			}
		}
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		try {
			Channel c = e.getChannel();
			Session session = this.session.get();

			if (!onClient) {
				Server server = (Server) engine;
				server.getChannelGroup().remove(c);
				server.getSessionRegistry().remove(session);
			}

			if (session.isPrimary(c)) {
				session.dispose();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Exception thrown when disconnecting", ex);
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Session session = this.session.get();
		if (session.isPrimary(ctx.getChannel())) {
			session.messageReceived((Message) e.getMessage());
		} else {
			session.messageReceivedOnAuxChannel(ctx.getChannel(), (Message) e.getMessage());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		Channel c = e.getChannel();
		if (c.isOpen()) {
			Session session = this.session.get();

			if (!onClient) {
				Server server = (Server) engine;
				server.getChannelGroup().remove(c);
				server.getSessionRegistry().remove(session);
			}

			if (session.isPrimary(c)) {
				session.dispose();
			}
			engine.getLogger().log(Level.WARNING, "Exception caught, closing channel: " + c + "...", e.getCause());
			c.close();
		}
	}

	public Session getSession() {
		return this.session.get();
	}

	public void setSession(Session session) {
		if (!this.session.compareAndSet(null, session)) {
			throw new IllegalStateException("Session may not be set more than once");
		}
		decoder.setProtocol(session.getProtocol());
		encoder.setProtocol(session.getProtocol());
	}
}
