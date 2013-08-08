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

import java.io.File;
import java.io.FilenameFilter;
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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.Validate;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.transport.spi.InitializationException;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.CommandSource;
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.Listener;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.engine.EngineStopEvent;
import org.spout.api.event.world.WorldLoadEvent;
import org.spout.api.event.world.WorldUnloadEvent;
import org.spout.api.generator.EmptyWorldGenerator;
import org.spout.api.generator.FlatWorldGenerator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.resource.FileSystem;
import org.spout.api.util.StringUtil;
import org.spout.api.util.access.AccessManager;
import org.spout.cereal.config.ConfigurationException;
import org.spout.engine.component.entity.SpoutPhysicsComponent;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.SpoutPlayerSnapshot;
import org.spout.engine.filesystem.ServerFileSystem;
import org.spout.engine.filesystem.versioned.PlayerFiles;
import org.spout.engine.filesystem.versioned.WorldFiles;
import org.spout.engine.listener.SpoutServerListener;
import org.spout.engine.protocol.PortBindingImpl;
import org.spout.engine.protocol.PortBindings;
import org.spout.engine.protocol.SpoutNioServerSocketChannel;
import org.spout.engine.protocol.SpoutServerSession;
import org.spout.engine.protocol.SpoutSessionRegistry;
import org.spout.engine.util.access.SpoutAccessManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutServerWorld;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.WorldSavingThread;

public class SpoutServer extends SpoutEngine implements Server {
	/**
	 * The {@link FileSystem} for the server
	 */
	private final ServerFileSystem filesystem = new ServerFileSystem();
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
	protected final SpoutSessionRegistry sessions = new SpoutSessionRegistry();
	protected final ChannelGroup group = new DefaultChannelGroup();
	/**
	 * The {@link AccessManager} for the Server.
	 */
	private final SpoutAccessManager accessManager = new SpoutAccessManager();
	protected final SnapshotableLinkedHashMap<String, SpoutPlayer> players = new SnapshotableLinkedHashMap<>(snapshotManager);
	private final SnapshotableLinkedHashMap<String, SpoutServerWorld> loadedWorlds = new SnapshotableLinkedHashMap<>(snapshotManager);
	private final WorldGenerator defaultGenerator = new EmptyWorldGenerator();
	private final Object jmdnsSync = new Object();
	private JmDNS jmdns = null;
	private final SessionTask sesionTask = new SessionTask();

	public SpoutServer() {
		logFile = "server-log-%D.txt";
	}

	@Override
	public void start() {
		start(true, new SpoutServerListener(this));
	}

	protected void start(boolean checkWorlds, Listener listener) {
		super.start();
		if (checkWorlds) {
			if (SpoutConfiguration.CREATE_FALLBACK_WORLD.getBoolean() && loadedWorlds.getLive().isEmpty()) {
				Spout.info("No worlds detected. Creating fallback world.");
				World world = loadWorld("fallback_world", new FlatWorldGenerator());
				world.setSpawnPoint(new Transform(new Point(world, 0, 5, 0), Quaternion.IDENTITY, Vector3.ONE));
				this.setDefaultWorld(world);
			} else {
				// Pick the default world from the configuration
				World world = this.getWorld(SpoutConfiguration.DEFAULT_WORLD.getString());
				if (world != null) {
					this.setDefaultWorld(world);
				}

				// If we don't have a default world set, just grab one.
				getDefaultWorld();
			}
		}
		getEventManager().registerEvents(listener, this);
		getEventManager().callEvent(new EngineStartEvent());
		filesystem.postStartup();
		filesystem.notifyInstalls();
		WorldSavingThread.startThread();
		Spout.info("Done Loading, ready for players.");
	}

	@Override
	protected void setupBindings(SpoutConfiguration config) {
		PortBindings portBindings = new PortBindings(this, config);
		try {
			portBindings.load(config);
			portBindings.bindAll();
			portBindings.save();
		} catch (ConfigurationException e) {
			Spout.severe("Error loading port bindings: ", e);
		}

		// UPnP
		if (SpoutConfiguration.UPNP.getBoolean()) {
			for (PortBinding binding : getBoundAddresses()) {
				if (binding.getAddress() instanceof InetSocketAddress) {
					mapUPnPPort(((InetSocketAddress) binding.getAddress()).getPort(), "Spout Server");
				}
			}
		}

		// Bonjour
		setupBonjour();

		if (boundProtocols.size() == 0) {
			Spout.warn("No port bindings registered! Clients will not be able to connect to the server.");
		}
	}

	@Override
	public void init(SpoutApplication args) {
		super.init(args);
		// Note: All threads are daemons, cleanup of the executors is handled by bootstrap.getFactory().releaseExternalResources(); in stop(...).
		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Worker", true));
		ChannelFactory factory = new SpoutNioServerSocketChannel(executorBoss, executorWorker);
		bootstrap.setFactory(factory);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory();
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
			Spout.severe("Failed to bind to address " + binding.getAddress() + ". Is there already another server running on this address?", ex);
			return false;
		}

		Spout.info("Binding to address: {0}...", binding.getAddress());
		return true;
	}

	@Override
	public ChannelGroup getChannelGroup() {
		return group;
	}

	@Override
	public SessionRegistry getSessionRegistry() {
		return sessions;
	}

	@Override
	protected Runnable getSessionTask() {
		return sesionTask;
	}

	private class SessionTask implements Runnable {
		@Override
		public void run() {
			sessions.pulse();
		}
	}

	@Override
	public int getMaxPlayers() {
		return SpoutConfiguration.MAXIMUM_PLAYERS.getInt();
	}

	public Collection<SpoutPlayer> rawGetAllOnlinePlayers() {
		return players.get().values();
	}

	public boolean removePlayer(SpoutPlayer player) {
		boolean remove = players.remove(player.getName(), player);
		if (remove) {
			if (reclamation != null) {
				reclamation.removePlayer();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean allowFlight() {
		return allowFlight;
	}

	@Override
	public List<PortBinding> getBoundAddresses() {
		List<PortBinding> bindings = new ArrayList<>();
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
		return new SpoutServerSession<>(this, channel, protocol);
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
				Spout.severe("Could not enable UPnP Service", e.getMessage());
			}
		}

		return upnpService;
	}

	private PortMapping createPortMapping(int port, PortMapping.Protocol protocol, String description) {
		try {
			return new PortMapping(port, InetAddress.getLocalHost().getHostAddress(), protocol, description);
		} catch (UnknownHostException e) {
			Error error = new Error("Error while trying to retrieve the localhost while creating a PortMapping object.", e);
			Spout.severe(e.getMessage(), e);
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
		ArrayList<SpoutPlayer> onlinePlayers = new ArrayList<>(playerList.size());
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
	public List<String> getAllPlayers() {
		ArrayList<String> names = new ArrayList<>();
		for (Player player : players.getValues()) {
			names.add(player.getName());
		}
		return Collections.unmodifiableList(names);
	}

	@Override
	public void copySnapshotRun() {
		super.copySnapshotRun();
		for (Player player : players.get().values()) {
			((SpoutPlayer) player).copySnapshot();
		}
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

	@Override
	public void startTickRun(int stage, long delta) {
		switch (stage) {
			case 0:
				getEngineItemMap().save();
				getEngineBiomeMap().save();
				getEngineLightingMap().save();
				break;
		}
	}

	@Override
	public Collection<World> matchWorld(String name) {
		return StringUtil.matchName(getWorlds(), name);
	}

	@Override
	public Collection<File> matchWorldFolder(String worldName) {
		return StringUtil.matchFile(getWorldFolders(), worldName);
	}

	@Override
	public SpoutServerWorld getWorld(String name) {
		return getWorld(name, true);
	}

	@Override
	public SpoutServerWorld getWorld(String name, boolean exact) {
		if (exact) {
			SpoutServerWorld world = loadedWorlds.get().get(name);
			if (world != null) {
				return world;
			}
			return loadedWorlds.getLive().get(name);
		} else {
			return StringUtil.getShortest(StringUtil.matchName(loadedWorlds.getValues(), name));
		}
	}

	@Override
	public SpoutServerWorld getWorld(UUID uid) {
		for (SpoutServerWorld world : loadedWorlds.getValues()) {
			if (world.getUID().equals(uid)) {
				return world;
			}
		}
		return null;
	}

	@Override
	public Collection<World> getWorlds() {
		Collection<World> w = new ArrayList<>();
		for (SpoutServerWorld world : loadedWorlds.getValues()) {
			w.add(world);
		}
		return w;
	}

	@Override
	public World loadWorld(String name, WorldGenerator generator) {
		if (loadedWorlds.get().containsKey((name))) {
			return loadedWorlds.get().get(name);
		}
		if (loadedWorlds.getLive().containsKey(name)) {
			return loadedWorlds.getLive().get(name);
		}

		// TODO: Should include generator (and non-zero seed)
		if (generator == null) {
			generator = defaultGenerator;
		}

		SpoutServerWorld world = WorldFiles.loadWorld(this, generator, name);

		World oldWorld = loadedWorlds.putIfAbsent(name, world);

		if (oldWorld != null) {
			return oldWorld;
		}

		if (!scheduler.addAsyncManager(world)) {
			throw new IllegalStateException("Unable to add world to the scheduler");
		}
		getEventManager().callDelayedEvent(new WorldLoadEvent(world));
		return world;
	}

	@Override
	public void save(boolean worlds, boolean players) {
		// TODO: Auto-generated method stub
	}

	@Override
	public List<File> getWorldFolders() {
		File[] folders = this.getWorldFolder().listFiles((FilenameFilter) DirectoryFileFilter.INSTANCE);
		if (folders == null || folders.length == 0) {
			return new ArrayList<>();
		}
		List<File> worlds = new ArrayList<>(folders.length);
		// Are they really world folders?
		for (File world : folders) {
			if (new File(world, "world.dat").exists()) {
				worlds.add(world);
			}
		}
		return worlds;
	}

	@Override
	public File getWorldFolder() {
		return ServerFileSystem.WORLDS_DIRECTORY;
	}

	@Override
	public WorldGenerator getDefaultGenerator() {
		return defaultGenerator;
	}

	@Override
	public World getDefaultWorld() {
		final Map<String, SpoutServerWorld> loadedWorlds = this.loadedWorlds.get();

		final World defaultWorld = this.defaultWorld.get();
		if (defaultWorld != null && loadedWorlds.containsKey(defaultWorld.getName())) {
			return defaultWorld;
		}

		if (loadedWorlds.isEmpty()) {
			return null;
		}

		World first = loadedWorlds.values().iterator().next();
		return first;
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		return unloadWorld(loadedWorlds.getLive().get(name), save);
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		if (world == null) {
			return false;
		}

		boolean success = loadedWorlds.remove(world.getName(), (SpoutServerWorld) world);
		if (success) {
			if (save) {
				SpoutServerWorld w = (SpoutServerWorld) world;
				if (!scheduler.removeAsyncManager(w)) {
					throw new IllegalStateException("Unable to remove world from scheduler when halting was attempted");
				}
				getEventManager().callDelayedEvent(new WorldUnloadEvent(world));
				w.unload(save);
			}
			// Note: Worlds should not allow being saved twice and/or throw exceptions if accessed after unloading.
			// Also, should blank out as much internal world data as possible, in case plugins retain references to unloaded worlds.
		}
		return success;
	}

	@Override
	public Entity getEntity(UUID uid) {
		for (World w : loadedWorlds.get().values()) {
			Entity e = w.getEntity(uid);
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutServerSession<?> session, int syncDistance) {
		Class<? extends PlayerNetworkComponent> network = session.getProtocol().getServerNetworkComponent(session);
		SpoutPlayerSnapshot snapshot = PlayerFiles.loadPlayerData(playerName);
		SpoutPlayer player;
		if (snapshot == null) {
			getLogger().info("First login for " + playerName + ", creating new player data");
			player = new SpoutPlayer(this, network, playerName, getDefaultWorld().getSpawnPoint());
		} else {
			player = new SpoutPlayer(this, network, snapshot);
		}
		session.setPlayer(player);
		player.getNetwork().setSession(session);
		//Set the player's sync distance
		player.getNetwork().setSyncDistance(syncDistance);
		player.getNetwork().forceSync();

		SpoutPlayer oldPlayer = players.put(playerName, player);

		if (reclamation != null) {
			reclamation.addPlayer();
		}

		if (oldPlayer != null && oldPlayer.getNetwork().getSession() != null) {
			oldPlayer.kick("Login occured from another client");
		}

		// Spawn the player in the world
		final SpoutPhysicsComponent physics = (SpoutPhysicsComponent) player.getPhysics();
		World world = physics.getTransformLive().getPosition().getWorld();
		world.spawnEntity(player);
		((SpoutServerWorld) world).addPlayer(player);

		// Initialize the session
		session.getProtocol().initializeServerSession(session);

		return player;
	}

	protected Collection<SpoutServerWorld> getLiveWorlds() {
		return loadedWorlds.getLive().values();
	}
}
