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
package org.spout.engine;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.spout.api.player.Player;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Session;
import org.spout.engine.listener.SpoutProxyConnectListener;
import org.spout.engine.listener.SpoutProxyListener;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.protocol.SpoutSession;

public class SpoutProxy extends SpoutServer {

	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ClientBootstrap clientBootstrap = new ClientBootstrap();

	@Override
	public void start() {
		super.start(false, new SpoutProxyListener(this));
	}

	@Override
	public Platform getPlatform() {
		return Platform.PROXY;
	}

	@Override
	public Player addPlayer(String playerName, SpoutSession session, int viewDistance) {

		SpoutPlayer player = null;

		while (true) {
			player = onlinePlayers.getLive().get(playerName);

			if (player != null) {
				break;
			}

			player = new SpoutPlayer(playerName, null, session);
			if (onlinePlayers.putIfAbsent(playerName, player) == null) {
				break;
			}
		}

		session.setPlayer(player);
		return player;
	}

	public void connect(String playerName, Session session) {
		connect("localhost", 25565, playerName, session);
	}

	public void connect(String hostname, int port, String playerName, Session session) {
		ChannelFutureListener listener = new SpoutProxyConnectListener(this, playerName, session);
		clientBootstrap.connect(new InetSocketAddress(hostname, port)).addListener(listener);
	}

	@Override
	public void init(Arguments args) {
		super.init(args);
		ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
		clientBootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
		clientBootstrap.setPipelineFactory(pipelineFactory);

		clientBootstrap.setOption("tcpNoDelay", true);
		clientBootstrap.setOption("keepAlive", true);
	}

	@Override
	public Session newSession(Channel channel) {
		return newSession(channel, true);
	}

	@Override
	public void stop() {
		super.stop();
		clientBootstrap.getFactory().releaseExternalResources();
	}
}
