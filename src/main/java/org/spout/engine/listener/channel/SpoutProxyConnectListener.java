/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.listener.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.protocol.CommonHandler;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;

import org.spout.engine.protocol.SpoutSession;

public class SpoutProxyConnectListener implements ChannelFutureListener {
	private final Engine engine;
	private final SpoutSession<?> session;
	private final String playerName;

	public SpoutProxyConnectListener(Engine engine, String playerName, Session session) {
		this.engine = engine;
		this.session = (SpoutSession<?>) session;
		this.playerName = playerName;
	}

	/**
	 * Gets the Engine that of this listener
	 * @return the Engine
	 */
	public Engine getEngine() {
		return this.engine;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (!future.isDone()) {
			throw new IllegalStateException("Connect operation was not done when listener was triggered");
		} else {
			Channel c = future.getChannel();
			if (future.isSuccess()) {
				Spout.getLogger().info("Connect to server successful " + c.getRemoteAddress() + ", " + playerName);
				session.bindAuxChannel(c);
				ChannelPipeline pipeline = c.getPipeline();
				if (pipeline != null) {
					CommonHandler d = pipeline.get(CommonHandler.class);
					if (d != null) {
						d.setSession(session);
					}
					Protocol protocol = session.getProtocol();
					if (protocol != null) {
						Message intro = protocol.getIntroductionMessage(playerName);
						c.write(intro);
						return;
					}
				}
				session.disconnect("Login failed for backend server");
			} else {
				Spout.getLogger().info("Failed to connect to server " + c.getRemoteAddress() + ", " + playerName);
				session.disconnect("Unable to connect to backend server");
			}
		}
	}
}
