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
package org.spout.engine;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import org.spout.api.Platform;
import org.spout.api.entity.Player;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.listener.SpoutProxyListener;
import org.spout.engine.listener.channel.SpoutProxyConnectListener;
import org.spout.engine.protocol.SpoutProxySession;
import org.spout.engine.protocol.SpoutServerSession;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;

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
	public Player addPlayer(String playerName, SpoutServerSession<?> session, int viewDistance) {
		SpoutPlayer player = new SpoutPlayer(this, playerName, null);
		players.putIfAbsent(playerName, player);
		session.setPlayer(player);
		return player;
	}

	public void connect(String playerName, Session session) {
		connect("localhost", 25565, playerName, session);
	}

	public void connect(String hostname, int port, String playerName, Session session) {
		ChannelFutureListener listener = new SpoutProxyConnectListener(this, playerName, session);
		InetSocketAddress addr = new InetSocketAddress(hostname, port);
		clientBootstrap.connect(addr).addListener(listener);
	}

	@Override
	public void init(SpoutApplication args) {
		super.init(args);
		//Note: All threads are daemons, cleanup of the executors is handled by clientBootstrap.getFactory().releaseExternalResources(); in stop(...).
		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Worker", true));
		ChannelFactory factory = new NioClientSocketChannelFactory(executorBoss, executorWorker);
		clientBootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
		clientBootstrap.setPipelineFactory(pipelineFactory);

		clientBootstrap.setOption("tcpNoDelay", true);
		clientBootstrap.setOption("keepAlive", true);
	}

	@Override
	public Session newSession(Channel channel) {
		Protocol protocol = getProtocol(channel.getLocalAddress());
		return new SpoutProxySession(this, channel, protocol);
	}

	@Override
	public boolean stop(String message) {
		if (!super.stop(message, false)) {
			return false;
		}
		Runnable finalTask = new Runnable() {
			@Override
			public void run() {
				clientBootstrap.getFactory().releaseExternalResources();
			}
		};
		getScheduler().submitFinalTask(finalTask, true);
		getScheduler().stop();
		return true;
	}
}
