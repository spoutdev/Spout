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

import static org.spout.api.lang.Translation.broadcast;
import static org.spout.api.lang.Translation.log;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.lang3.Validate;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;

import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.entity.Player;
import org.spout.api.event.server.ServerStopEvent;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;

import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.listener.SpoutServerListener;
import org.spout.engine.protocol.PortBindingImpl;
import org.spout.engine.protocol.PortBindings;
import org.spout.engine.protocol.SpoutNioServerSocketChannel;
import org.spout.engine.protocol.SpoutServerSession;
import org.spout.engine.util.bans.FlatFileBanManager;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;
import org.teleal.cling.transport.spi.InitializationException;

import org.spout.api.Server;
import org.spout.api.event.Listener;
import org.spout.api.event.server.ServerStartEvent;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.Session;

import org.spout.engine.filesystem.ServerFileSystem;

import org.spout.api.util.StringUtil;
import org.spout.api.util.ban.BanManager;

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

	/**
	 * The UPnP service
	 */
	private UpnpService upnpService;

	public SpoutServer() {
		this.filesystem = new ServerFileSystem();
	}

	@Override
	public void start() {
		start(true);
	}

	@Override
	public void start(boolean checkWorlds) {
		start(checkWorlds, new SpoutServerListener(this));
	}

	public void start(boolean checkWorlds, Listener listener) {
		super.start(checkWorlds);

		banManager = new FlatFileBanManager();

		getEventManager().registerEvents(listener, this);
		getFilesystem().postStartup();
		getEventManager().callEvent(new ServerStartEvent());
		log("Done Loading, ready for players.");
	}

	@Override
	protected void postPluginLoad() {
		PortBindings portBindings = new PortBindings(this, config);
		try {
			portBindings.load(config);
			portBindings.bindAll();
			portBindings.save();
		} catch (ConfigurationException e) {
			log("Error loading port bindings: %0", Level.SEVERE, e);
		}

		if (boundProtocols.size() == 0) {
			log("No port bindings registered! Clients will not be able to connect to the server.", Level.WARNING);
		}
	}

	@Override
	public void init(SpoutApplication args) {
		super.init(args);
		//Note: All threads are daemons, cleanup of the executors is handled by bootstrap.getFactory().releaseExternalResources(); in stop(...).
		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Worker", true));
		ChannelFactory factory = new SpoutNioServerSocketChannel(executorBoss, executorWorker);
		bootstrap.setFactory(factory);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, false);
		bootstrap.setPipelineFactory(pipelineFactory);
	}

	@Override
	public boolean stop(final String message) {
		Runnable finalTask = new Runnable() {
			public void run() {
				for (Player player : getOnlinePlayers()) {
					ServerStopEvent stopEvent = new ServerStopEvent(message);
					getEventManager().callEvent(stopEvent);
					player.kick(stopEvent.getMessage());
				}

				if (upnpService != null) {
					upnpService.shutdown();
				}
				bootstrap.getFactory().releaseExternalResources();
				boundProtocols.clear();
			}
		};
		scheduler.submitFinalTask(finalTask);
		scheduler.stop(1);
		return super.stop(message);
	}

	@Override
	public boolean bind(PortBinding binding) {
		Validate.notNull(binding);
		if (binding.getProtocol() == null) {
			throw new IllegalArgumentException("Protocol cannot be null");
		}

		if (boundProtocols.containsKey(binding.getAddress())) {
			return false;
		}
		boundProtocols.put(binding.getAddress(), binding.getProtocol());
		try {
			group.add(bootstrap.bind(binding.getAddress()));
		} catch (org.jboss.netty.channel.ChannelException ex) {
			log("Failed to bind to address %0. Is there already another server running on this address?", Level.SEVERE, binding.getAddress(), ex);
			return false;
		}

		log("Binding to address: %0...", binding.getAddress());
		return true;
	}

	@Override
	public void banPlayer(String player) {
		banPlayer(player, true);
	}

	@Override
	public void banPlayer(String player, boolean kick) {
		banPlayer(player, kick, null);
	}

	@Override
	public void banPlayer(String player, boolean kick, Object... reason) {
		Player p = getPlayer(player, true);
		if (kick && p != null) {
			if (reason == null) {
				p.kick();
			} else {
				p.kick(reason);
			}
		}
		banManager.setBanned(player, true);
	}

	@Override
	public void banPlayer(Player player) {
		banPlayer(player, true);
	}

	@Override
	public void banPlayer(Player player, boolean kick) {
		banPlayer(player, kick, null);
	}

	@Override
	public void banPlayer(Player player, boolean kick, Object... reason) {
		if (kick) {
			if (reason == null) {
				player.kick();
			} else {
				player.kick(reason);
			}
		}
		banManager.setBanned(player.getName(), true);
	}

	@Override
	public void unbanPlayer(String player) {
		banManager.setBanned(player, false);
	}

	@Override
	public void banIp(String address) {
		banIp(address, true);
	}

	@Override
	public void banIp(String address, boolean kick) {
		banIp(address, kick, null);
	}

	@Override
	public void banIp(String address, boolean kick, Object... reason) {
		if (kick) {
			for (Player player : getOnlinePlayers()) {
				if (player.getAddress().getHostAddress().equals(address)) {
					if (reason == null) {
						player.kick();
					} else {
						player.kick(reason);
					}
				}
			}
		}
		banManager.setIpBanned(address, true);
	}

	@Override
	public void unbanIp(String address) {
		banManager.setIpBanned(address, false);
	}

	@Override
	public Collection<String> getBannedIps() {
		return banManager.getBannedIps();
	}

	@Override
	public Collection<String> getBannedPlayers() {
		return banManager.getBannedPlayers();
	}

	@Override
	public boolean isBanned(String player) {
		return banManager.isBanned(player);
	}

	@Override
	public boolean isIpBanned(String address) {
		return banManager.isIpBanned(address);
	}

	@Override
	public ChatArguments getBanMessage() {
		return banManager.getBanMessage();
	}

	@Override
	public void setBanMessage(Object... message) {
		banManager.setBanMessage(message);
	}

	@Override
	public ChatArguments getIpBanMessage() {
		return banManager.getIpBanMessage();
	}

	@Override
	public void setIpBanMessage(Object... message) {
		banManager.setIpBanMessage(message);
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

	public List<PortBinding> getBoundAddresses() {
		List<PortBinding> bindings = new ArrayList<PortBinding>();
		for (Map.Entry<SocketAddress, Protocol> entry : boundProtocols.entrySet()) {
			bindings.add(new PortBindingImpl(entry.getValue(), entry.getKey()));
		}
		return Collections.unmodifiableList(bindings);
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
	public Session newSession(Channel channel) {
		Protocol protocol = getProtocol(channel.getLocalAddress());
		return new SpoutServerSession<SpoutServer>(this, channel, protocol);
	}

	@Override
	public Platform getPlatform() {
		return Platform.SERVER;
	}

	@Override
	public String getName() {
		return name;
	}

	private UpnpService getUPnPService() {
		if (upnpService == null) {
			try {
				upnpService = new UpnpServiceImpl();
			} catch (InitializationException e) {
				log("Could not enable UPnP Service: %0", Level.SEVERE, e.getMessage());
			}
		}

		return upnpService;
	}

	private PortMapping createPortMapping(int port, PortMapping.Protocol protocol, String description) {
		try {
			return new PortMapping(port, InetAddress.getLocalHost().getHostAddress(), protocol, description);
		} catch (UnknownHostException e) {
			Error error = new Error("Error while trying to retrieve the localhost while creating a PortMapping object.", e);
			getLogger().severe(e.getMessage());
			throw error;
		}
	}

	@Override
	public void mapUPnPPort(int port) {
		mapUPnPPort(port, null);
	}

	@Override
	public void mapUPnPPort(int port, String description) {
		UpnpService upnpService = getUPnPService();
		if (upnpService != null) {
			PortMapping[] desiredMapping = {createPortMapping(port, PortMapping.Protocol.TCP, description), createPortMapping(port, PortMapping.Protocol.UDP, description)};
			PortMappingListener listener = new PortMappingListener(desiredMapping);

			ControlPoint controlPoint = upnpService.getControlPoint();
			controlPoint.getRegistry().addListener(listener);
			controlPoint.search();
		}
	}

	@Override
	public void mapTCPPort(int port) {
		mapTCPPort(port, null);
	}

	@Override
	public void mapTCPPort(int port, String description) {
		PortMapping desiredMapping = createPortMapping(port, PortMapping.Protocol.TCP, description);
		PortMappingListener listener = new PortMappingListener(desiredMapping);

		ControlPoint controlPoint = getUPnPService().getControlPoint();
		controlPoint.getRegistry().addListener(listener);
		controlPoint.search();
	}

	@Override
	public void mapUDPPort(int port) {
		mapUDPPort(port, null);
	}

	@Override
	public void mapUDPPort(int port, String description) {
		PortMapping desiredMapping = createPortMapping(port, PortMapping.Protocol.UDP, description);
		PortMappingListener listener = new PortMappingListener(desiredMapping);

		ControlPoint controlPoint = getUPnPService().getControlPoint();
		controlPoint.getRegistry().addListener(listener);
		controlPoint.search();
	}

	@Override
	public List<String> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpoutPlayer[] getOnlinePlayers() {
		Map<String, SpoutPlayer> playerList = onlinePlayers.get();
		ArrayList<SpoutPlayer> onlinePlayers = new ArrayList<SpoutPlayer>(playerList.size());
		for (SpoutPlayer player : playerList.values()) {
			if (player.isOnline()) {
				onlinePlayers.add(player);
			}
		}
		return onlinePlayers.toArray(new SpoutPlayer[onlinePlayers.size()]);
	}

	@Override
	public void broadcastMessage(Object... message) {
		broadcastMessage(STANDARD_BROADCAST_PERMISSION, message);
	}

	@Override
	public void broadcastMessage(String permission, Object... message) {
		ChatArguments args = new ChatArguments(message);
		for (PermissionsSubject player : getAllWithNode(permission)) {
			if (player instanceof CommandSource) {
				((CommandSource) player).sendMessage(args);
			}
		}
	}

	@Override
	public Player getPlayer(String name, boolean exact) {
		name = name.toLowerCase();
		if (exact) {
			for (Player player : onlinePlayers.getValues()) {
				if (player.getName().equalsIgnoreCase(name)) {
					return player;
				}
			}
			return null;
		} else {
			return StringUtil.getShortest(StringUtil.matchName(onlinePlayers.getValues(), name));
		}
	}

	@Override
	public Collection<Player> matchPlayer(String name) {
		return StringUtil.matchName(Arrays.<Player>asList(getOnlinePlayers()), name);
	}
}
