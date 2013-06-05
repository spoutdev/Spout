/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;
import org.teleal.cling.transport.spi.InitializationException;

import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.command.CommandSource;
import org.spout.api.entity.Player;
import org.spout.api.event.Listener;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.engine.EngineStopEvent;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.resource.FileSystem;
import org.spout.api.util.StringUtil;
import org.spout.api.util.access.AccessManager;

import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.CommonFileSystem;
import org.spout.engine.listener.SpoutServerListener;
import org.spout.engine.protocol.PortBindingImpl;
import org.spout.engine.protocol.PortBindings;
import org.spout.engine.protocol.SpoutNioServerSocketChannel;
import org.spout.engine.protocol.SpoutServerSession;
import org.spout.engine.util.access.SpoutAccessManager;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.WorldSavingThread;

import static org.spout.api.lang.Translation.log;

public class SpoutServer extends SpoutEngine implements Server {
	/**
	 * The {@link FileSystem} for the server
	 */
	private final FileSystem filesystem;
	/**
	 * If the server allows flight.
	 */
	private volatile boolean allowFlight = false;
	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ServerBootstrap bootstrap = new ServerBootstrap();
	/**
	 * The UPnP service
	 */
	private UpnpService upnpService;
	/**
	 * The {@link AccessManager} for the Server.
	 */
	private final SpoutAccessManager accessManager = new SpoutAccessManager();
	private final Object jmdnsSync = new Object();
	private JmDNS jmdns = null;

	public SpoutServer() {
		this.filesystem = new CommonFileSystem();
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
		getEventManager().registerEvents(listener, this);
		getEventManager().callEvent(new EngineStartEvent());
		filesystem.postStartup();
		WorldSavingThread.startThread();
		log("Done Loading, ready for players.");
	}

	@Override
	protected void setupBindings(SpoutConfiguration config) {
		PortBindings portBindings = new PortBindings(this, config);
		try {
			portBindings.load(config);
			portBindings.bindAll();
			portBindings.save();
		} catch (ConfigurationException e) {
			log("Error loading port bindings: %0", Level.SEVERE, e);
		}

		//UPnP
		if (SpoutConfiguration.UPNP.getBoolean()) {
			for (PortBinding binding : getBoundAddresses()) {
				if (binding.getAddress() instanceof InetSocketAddress) {
					mapUPnPPort(((InetSocketAddress) binding.getAddress()).getPort(), "Spout Server");
				}
			}
		}

		//Bonjour
		setupBonjour();

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

		accessManager.load();
		accessManager.setWhitelistEnabled(SpoutConfiguration.WHITELIST_ENABLED.getBoolean());
	}

	@Override
	public boolean stop(final String message) {
		return stop(message, true);
	}

	@Override
	public boolean stop(final String message, boolean stopScheduler) {
		if (!super.stop(message, false)) {
			return false;
		}
		final SpoutServer engine = this;
		Runnable lastTickTask = new Runnable() {
			@Override
			public void run() {
				EngineStopEvent stopEvent = new EngineStopEvent(message);
				getEventManager().callEvent(stopEvent);
				for (Player player : getOnlinePlayers()) {
					((SpoutPlayer) player).kick(true, stopEvent.getMessage());
				}
				if (upnpService != null) {
					upnpService.shutdown();
				}
				closeBonjour();
				for (SpoutWorld world : engine.getLiveWorlds()) {
					world.unload(true);
				}
			}
		};

		Runnable finalTask = new Runnable() {
			@Override
			public void run() {
				ChannelGroupFuture f = group.close();
				try {
					f.await();
				} catch (InterruptedException ie) {
					getLogger().info("Thread interrupted when waiting for network shutdown");
				}
				WorldSavingThread.finish();
				WorldSavingThread.staticJoin();

				bootstrap.getFactory().releaseExternalResources();
				boundProtocols.clear();
			}
		};

		getScheduler().submitLastTickTask(lastTickTask);
		getScheduler().submitFinalTask(finalTask, false);
		getScheduler().stop();
		return true;
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
			getChannelGroup().add(bootstrap.bind(binding.getAddress()));
		} catch (org.jboss.netty.channel.ChannelException ex) {
			log("Failed to bind to address %0. Is there already another server running on this address?", Level.SEVERE, binding.getAddress(), ex);
			return false;
		}

		log("Binding to address: %0...", binding.getAddress());
		return true;
	}

	@Override
	public int getMaxPlayers() {
		return SpoutConfiguration.MAXIMUM_PLAYERS.getInt();
	}

	@Override
	public boolean allowFlight() {
		return allowFlight;
	}

	@Override
	public List<PortBinding> getBoundAddresses() {
		List<PortBinding> bindings = new ArrayList<PortBinding>();
		for (Map.Entry<SocketAddress, Protocol> entry : boundProtocols.entrySet()) {
			bindings.add(new PortBindingImpl(entry.getValue(), entry.getKey()));
		}
		return Collections.unmodifiableList(bindings);
	}

	@Override
	public Session newSession(Channel channel) {
		Protocol protocol = getProtocol(channel.getLocalAddress());
		if (SpoutConfiguration.SHOW_CONNECTIONS.getBoolean()) {
			getLogger().info("Downstream channel connected: " + channel + ".");
		}
		return new SpoutServerSession<SpoutServer>(this, channel, protocol);
	}

	@Override
	public Platform getPlatform() {
		return Platform.SERVER;
	}

	@Override
	public String getName() {
		return "Spout Server";
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
	public SpoutPlayer[] getOnlinePlayers() {
		Map<String, SpoutPlayer> playerList = players.get();
		ArrayList<SpoutPlayer> onlinePlayers = new ArrayList<SpoutPlayer>(playerList.size());
		for (SpoutPlayer player : playerList.values()) {
			if (player.isOnline()) {
				onlinePlayers.add(player);
			}
		}
		return onlinePlayers.toArray(new SpoutPlayer[onlinePlayers.size()]);
	}

	@Override
	public void broadcastMessage(String message) {
		broadcastMessage(STANDARD_BROADCAST_PERMISSION, message);
	}

	@Override
	public void broadcastMessage(String permission, String message) {
		for (PermissionsSubject subject : getAllWithNode(permission)) {
			if (subject instanceof CommandSource) {
				((CommandSource) subject).sendMessage(message);
			}
		}
	}

	@Override
	public Player getPlayer(String name, boolean exact) {
		name = name.toLowerCase();
		if (exact) {
			for (Player player : players.getValues()) {
				if (player.getName().equalsIgnoreCase(name)) {
					return player;
				}
			}
			return null;
		} else {
			return StringUtil.getShortest(StringUtil.matchName(players.getValues(), name));
		}
	}

	@Override
	public Collection<Player> matchPlayer(String name) {
		return StringUtil.matchName(Arrays.<Player>asList(getOnlinePlayers()), name);
	}

	@Override
	public AccessManager getAccessManager() {
		return accessManager;
	}

	@Override
	public FileSystem getFileSystem() {
		return filesystem;
	}

	private void setupBonjour() {
		if (SpoutConfiguration.BONJOUR.getBoolean()) {
			getScheduler().scheduleAsyncTask(this, new Runnable() {
				@Override
				public void run() {
					synchronized (jmdnsSync) {
						try {
							getLogger().info("Starting Bonjour Service Discovery");
							jmdns = JmDNS.create();
							for (PortBinding binding : getBoundAddresses()) {
								if (binding.getAddress() instanceof InetSocketAddress) {
									int port = ((InetSocketAddress) binding.getAddress()).getPort();
									ServiceInfo info = ServiceInfo.create("pipework._tcp.local.", "Spout Server", port, "");
									jmdns.registerService(info);
									getLogger().info("Started Bonjour Service Discovery on port: " + port);
								}
							}
						} catch (IOException e) {
							getLogger().log(Level.WARNING, "Failed to start Bonjour Service Discovery Library", e);
						}
					}
				}
			});
		}
	}

	private void closeBonjour() {
		if (jmdns != null) {
			getLogger().info(">>> Shutting down Bonjour service...");
			final JmDNS jmdns = this.jmdns;
			try {
				jmdns.close();
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Failed to stop Bonjour Service Discovery Library", e);
			}
			getLogger().info("<<< Bonjour service shutdown completed.");
		}
	}
}
