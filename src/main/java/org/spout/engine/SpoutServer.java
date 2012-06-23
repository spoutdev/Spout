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

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.event.Listener;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.engine.filesystem.ServerFileSystem;
import org.spout.engine.listener.SpoutListener;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.util.bans.BanManager;
import org.spout.engine.util.bans.FlatFileBanManager;

import com.beust.jcommander.JCommander;

public class SpoutServer extends SpoutEngine implements Server {
	private final String name = "Spout Server";

	private volatile int maxPlayers = 20;
	/**
	 * If the server has a whitelist or not.
	 */
	private volatile boolean whitelist = false;
	/**
	 * If the server allows flight.
	 */
	private volatile boolean allowFlight = false;
	/**
	 * A list of all players who can log onto this server, if using a whitelist.
	 */
	private List<String> whitelistedPlayers = new ArrayList<String>();
	/**
	 * The server's ban manager
	 */
	private BanManager banManager;
	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ServerBootstrap bootstrap = new ServerBootstrap();

	public SpoutServer() {
		this.filesystem = new ServerFileSystem();
	}

	public static void main(String[] args) {
		SpoutServer server = new SpoutServer();
		Spout.setEngine(server);
		Spout.getFilesystem().init();
		new JCommander(server, args);
		server.init(args);
		server.start();
	}

	public void start() {
		start(true);
	}
	
	public void start(boolean checkWorlds) {
		start(checkWorlds, new SpoutListener(this));
	}
		
	public void start(boolean checkWorlds, Listener listener) {
		super.start(checkWorlds);
		
		banManager = new FlatFileBanManager(this);

		getEventManager().registerEvents(listener, this);

		getLogger().info("Done Loading, ready for players.");
	}

	@Override
	public void init(String[] args) {
		super.init(args);
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);
	}

	@Override
	public void stop() {
		super.stop();
		bootstrap.getFactory().releaseExternalResources();
	}

	/**
	 * Binds this server to the specified address.
	 * @param address The addresss.
	 */
	@Override
	public boolean bind(SocketAddress address, BootstrapProtocol protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol cannot be null");
		}
		if (bootstrapProtocols.containsKey(address)) {
			return false;
		}
		bootstrapProtocols.put(address, protocol);
		group.add(bootstrap.bind(address));
		logger.log(Level.INFO, "Binding to address: {0}...", address);
		return true;
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public void save(boolean worlds, boolean players) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean allowFlight() {
		return allowFlight;
	}

	@Override
	public boolean isWhitelist() {
		return whitelist;
	}

	@Override
	public void setWhitelist(boolean whitelist) {
		this.whitelist = whitelist;
	}

	@Override
	public void updateWhitelist() {
		List<String> whitelist = SpoutConfiguration.WHITELIST.getStringList();
		if (whitelist != null) {
			whitelistedPlayers = whitelist;
		} else {
			whitelistedPlayers = new ArrayList<String>();
		}
	}

	@Override
	public String[] getWhitelistedPlayers() {
		String[] whitelist = new String[whitelistedPlayers.size()];
		for (int i = 0; i < whitelist.length; i++) {
			whitelist[i] = whitelistedPlayers.get(i);
		}
		return whitelist;
	}

	@Override
	public void whitelist(String player) {
		whitelistedPlayers.add(player);
		List<String> whitelist = SpoutConfiguration.WHITELIST.getStringList();
		if (whitelist == null) {
			whitelist = whitelistedPlayers;
		} else {
			whitelist.add(player);
		}
		SpoutConfiguration.WHITELIST.setValue(whitelist);
	}

	@Override
	public void unWhitelist(String player) {
		whitelistedPlayers.remove(player);
	}

	@Override
	public Collection<String> getIPBans() {
		return banManager.getIpBans();
	}

	@Override
	public void banIp(String address) {
		banManager.setIpBanned(address, true);
	}

	@Override
	public void unbanIp(String address) {
		banManager.setIpBanned(address, false);
	}

	@Override
	public void banPlayer(String player) {
		banManager.setBanned(player, true);
	}

	@Override
	public void unbanPlayer(String player) {
		banManager.setBanned(player, false);
	}

	@Override
	public boolean isBanned(String player, String address) {
		return banManager.isBanned(player, address);
	}

	@Override
	public boolean isIpBanned(String address) {
		return banManager.isIpBanned(address);
	}

	@Override
	public boolean isPlayerBanned(String player) {
		return banManager.isBanned(player);
	}

	@Override
	public String getBanMessage(String player) {
		return banManager.getBanMessage(player);
	}

	@Override
	public String getIpBanMessage(String address) {
		return banManager.getIpBanMessage(address);
	}

	@Override
	public Collection<String> getBannedPlayers() {
		return banManager.getBans();
	}

	@Override
	public Session newSession(Channel channel) {
		BootstrapProtocol protocol = getBootstrapProtocol(channel.getLocalAddress());
		return new SpoutSession(this, channel, protocol);
	}
	
	@Override
	public Platform getPlatform() {
		return Platform.SERVER;
	}

	@Override
	public String getName() {
		return name;
	}
}
