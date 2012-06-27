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
import org.spout.api.Spout;
import org.spout.api.player.Player;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Session;
import org.spout.engine.listener.SpoutProxyConnectListener;
import org.spout.engine.listener.SpoutProxyListener;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.protocol.SpoutSession;

import com.beust.jcommander.JCommander;

public class SpoutProxy extends SpoutServer {
	
	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ClientBootstrap clientBootstrap = new ClientBootstrap();
	
	public static void main(String[] args) {
		SpoutProxy proxy = new SpoutProxy();
		Spout.setEngine(proxy);
		Spout.getFilesystem().init();
		new JCommander(proxy, args);
		proxy.init(args);
		proxy.start();
	}
	
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
		
		return player;
	}
	
	public void connect(String playerName, Session session) {
		String hostname = "localhost";
		int port = 25565;
		ChannelFutureListener listener = new SpoutProxyConnectListener(this, playerName, session);
		clientBootstrap.connect(new InetSocketAddress(hostname, port)).addListener(listener);
	}
	
	@Override
	public void init(String[] args) {
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
