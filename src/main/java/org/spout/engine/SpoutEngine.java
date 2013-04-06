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

import java.io.File;
import java.io.FilenameFilter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import org.spout.api.Engine;
import org.spout.api.Platform;
import org.spout.api.chat.channel.ChatChannelFactory;
import org.spout.api.chat.completion.CompletionManager;
import org.spout.api.chat.completion.CompletionManagerImpl;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.EventManager;
import org.spout.api.event.SimpleEventManager;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;
import org.spout.api.event.world.WorldLoadEvent;
import org.spout.api.event.world.WorldUnloadEvent;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.generator.EmptyWorldGenerator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeRegistry;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.recipe.RecipeManager;
import org.spout.api.inventory.recipe.SimpleRecipeManager;
import org.spout.api.lighting.LightingRegistry;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.permissions.DefaultPermissions;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.plugin.CommonPluginLoader;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.CommonServiceManager;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginManager;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.plugin.security.CommonSecurityManager;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.util.StringMap;
import org.spout.api.util.StringUtil;

import org.spout.engine.chat.SpoutChatChannelFactory;
import org.spout.engine.chat.console.ConsoleManager;
import org.spout.engine.command.ClientCommands;
import org.spout.engine.command.CommonCommands;
import org.spout.engine.command.InputCommands;
import org.spout.engine.command.MessagingCommands;
import org.spout.engine.command.RendererCommands;
import org.spout.engine.command.ServerCommands;
import org.spout.engine.command.SyncedRootCommand;
import org.spout.engine.command.TestCommands;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.component.SpoutSceneComponent;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.filesystem.versioned.PlayerFiles;
import org.spout.engine.filesystem.versioned.WorldFiles;
import org.spout.engine.input.SpoutInputConfiguration;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.protocol.SpoutSessionRegistry;
import org.spout.engine.protocol.builtin.SpoutProtocol;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.DeadlockMonitor;
import org.spout.engine.util.TicklockMonitor;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableReference;
import org.spout.engine.world.MemoryReclamationThread;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.WorldSavingThread;

import static org.spout.api.lang.Translation.log;
import static org.spout.api.lang.Translation.tr;

public abstract class SpoutEngine implements AsyncManager, Engine {
	private static final Logger logger = Logger.getLogger("Spout");
	private final CommonSecurityManager securityManager = new CommonSecurityManager(0); //TODO Need to integrate this/evaluate security in the engine.
	private final CommonPluginManager pluginManager = new CommonPluginManager(this, securityManager, 0.0);
	private final ConsoleManager consoleManager;
	private final EventManager eventManager = new SimpleEventManager();
	private final RecipeManager recipeManager = new SimpleRecipeManager();
	private final ServiceManager serviceManager = CommonServiceManager.getInstance();
	private final SnapshotManager snapshotManager = new SnapshotManager();
	protected final SnapshotableLinkedHashMap<String, SpoutPlayer> players = new SnapshotableLinkedHashMap<String, SpoutPlayer>(snapshotManager);
	private final WorldGenerator defaultGenerator = new EmptyWorldGenerator();
	protected final SpoutSessionRegistry sessions = new SpoutSessionRegistry();
	protected final SpoutScheduler scheduler = new SpoutScheduler(this);
	protected final SpoutParallelTaskManager parallelTaskManager = new SpoutParallelTaskManager(this);
	protected final ChannelGroup group = new DefaultChannelGroup();
	private final AtomicBoolean setupComplete = new AtomicBoolean(false);
	private final SpoutConfiguration config = new SpoutConfiguration();
	private final SpoutInputConfiguration inputConfig = new SpoutInputConfiguration();
	private final CompletionManager completions = new CompletionManagerImpl();
	private final SyncedRootCommand rootCommand = new SyncedRootCommand(this);
	private final SnapshotableLinkedHashMap<String, SpoutWorld> loadedWorlds = new SnapshotableLinkedHashMap<String, SpoutWorld>(snapshotManager);
	private final SnapshotableReference<World> defaultWorld = new SnapshotableReference<World>(snapshotManager, null);
	protected final ConcurrentMap<SocketAddress, Protocol> boundProtocols = new ConcurrentHashMap<SocketAddress, Protocol>();
	protected final SnapshotableLinkedHashMap<String, SpoutPlayer> onlinePlayers = new SnapshotableLinkedHashMap<String, SpoutPlayer>(snapshotManager);
	private String logFile;
	private StringMap engineItemMap = null;
	private StringMap engineBiomeMap = null;
	private StringMap engineLightingMap = null;
	private SpoutApplication arguments;
	private MemoryReclamationThread reclamation = null;
	private DefaultPermissions defaultPerms;
	private ChatChannelFactory chatChannelFactory = new SpoutChatChannelFactory();

	public SpoutEngine() {
		logFile = "log-%D.txt";
		consoleManager = new ConsoleManager(this);
	}

	public void init(SpoutApplication args) {
		this.arguments = args;
		try {
			config.load();
			inputConfig.load();
		} catch (ConfigurationException e) {
			log("Error loading config: %0", Level.SEVERE, e.getMessage(), e);
		}

		consoleManager.setupConsole();

		scheduler.addAsyncManager(this);

		defaultPerms = new DefaultPermissions(this, new File(SharedFileSystem.getConfigDirectory(), "permissions.yml"));
		getDefaultPermissions().addDefaultPermission(STANDARD_BROADCAST_PERMISSION);
		getDefaultPermissions().addDefaultPermission(STANDARD_CHAT_PREFIX + "*");

		if (debugMode()) {
			new TicklockMonitor().start();
			new DeadlockMonitor().start();
		}
	}

	@Override
	public String getAPIVersion() {
		return SpoutEngine.class.getPackage().getImplementationVersion();
	}

	public abstract void start();

	public void start(boolean checkWorlds) {
		log("Spout is starting in %0-only mode.", getPlatform().name().toLowerCase());
		log("Current version is %0 (Implementing SpoutAPI %1).", getVersion(), getAPIVersion());
		log("This software is currently in alpha status so components may");
		log("have bugs or not work at all. Please report any issues to");
		log("http://issues.spout.org");

		if (debugMode()) {
			log("Debug Mode has been toggled on!  This mode is intended for developers only", Level.WARNING);
		}

		scheduler.scheduleSyncRepeatingTask(this, new SessionTask(sessions), 50, 50, TaskPriority.CRITICAL);

		final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(this, new SimpleInjector(this));

		// Register commands
		switch (getPlatform()) {
			case CLIENT:
				getRootCommand().addSubCommands(this, ClientCommands.class, commandRegFactory);
				break;
			case SERVER:
				getRootCommand().addSubCommands(this, ServerCommands.class, commandRegFactory);
				break;
			default:
				getRootCommand().addSubCommands(this, CommonCommands.class, commandRegFactory);
		}

		getRootCommand().addSubCommands(this, MessagingCommands.class, commandRegFactory);
		InputCommands.setupInputCommands(this, getRootCommand());

		if (debugMode()) {
			getRootCommand().addSubCommands(this, TestCommands.class, commandRegFactory);
		}

		if (getPlatform() == Platform.CLIENT) {
			getRootCommand().addSubCommands(this, RendererCommands.class, commandRegFactory);
		}
		Protocol.registerProtocol(new SpoutProtocol());

		//Setup the Material Registry
		engineItemMap = MaterialRegistry.setupRegistry();
		//Setup the Biome Registry
		engineBiomeMap = BiomeRegistry.setupRegistry();
		//Setup the Lighting Registry
		engineLightingMap = LightingRegistry.setupRegistry();

		// Start loading plugins
		loadPlugins();
		postPluginLoad(config);
		enablePlugins();

		if (checkWorlds) {
			//At least one plugin should have registered atleast one world
			if (loadedWorlds.getLive().size() == 0) {
				throw new IllegalStateException("There are no loaded worlds! You must install a plugin that creates a world (Did you forget Vanilla?)");
			}

			//Pick the default world from the configuration
			World world = this.getWorld(SpoutConfiguration.DEFAULT_WORLD.getString());
			if (world != null) {
				this.setDefaultWorld(world);
			}

			//If we don't have a default world set, just grab one.
			getDefaultWorld();
		}

		if (SpoutConfiguration.RECLAIM_MEMORY.getBoolean()) {
			reclamation = new MemoryReclamationThread();
			reclamation.start();
		}

		scheduler.startMainThread();
		WorldSavingThread.startThread();
		setupComplete.set(true);
	}

	/**
	 * This method is called after {@link #loadPlugins()} but before {@link #enablePlugins()}
	 */
	protected void postPluginLoad(SpoutConfiguration config) {
	}

	public void loadPlugins() {
		pluginManager.registerPluginLoader(CommonPluginLoader.class);
		pluginManager.clearPlugins();

		List<Plugin> plugins = pluginManager.loadPlugins(SharedFileSystem.getPluginDirectory());

		for (Plugin plugin : plugins) {
			try {
				//Technically unsafe.  This should call the security manager
				plugin.onLoad();
			} catch (Exception ex) {
				//TODO: fix
				//log("Error loading %0: %1", Level.SEVERE, plugin.getDescription().getName(), ex.getMessage(), ex);
				ex.printStackTrace();
			}
		}
	}

	public SpoutApplication getArguments() {
		return arguments;
	}

	private void enablePlugins() {
		for (Plugin plugin : pluginManager.getPlugins()) {
			pluginManager.enablePlugin(plugin);
		}
	}

	public Collection<SpoutPlayer> rawGetAllOnlinePlayers() {
		return players.get().values();
	}

	@Override
	public String getName() {
		return "Spout Engine";
	}

	@Override
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	@Override
	public Set<PermissionsSubject> getAllWithNode(String permission) {
		return getEventManager().callEvent(new PermissionGetAllWithNodeEvent(permission)).getAllowedReceivers();
	}

	@Override
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public File getUpdateFolder() {
		return SharedFileSystem.getUpdateDirectory();
	}

	@Override
	public File getConfigFolder() {
		return SharedFileSystem.getConfigDirectory();
	}

	@Override
	public File getDataFolder() {
		File dataDir = SharedFileSystem.getDataDirectory();
		File playerDirectory = new File(dataDir, "players");
		if (!playerDirectory.exists()) {
			playerDirectory.mkdirs();
		}
		return dataDir;
	}

	@Override
	public File getPluginFolder() {
		return SharedFileSystem.getPluginDirectory();
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
	public SpoutWorld getWorld(String name) {
		return getWorld(name, true);
	}

	@Override
	public SpoutWorld getWorld(String name, boolean exact) {
		if (exact) {
			SpoutWorld world = loadedWorlds.get().get(name);
			if (world != null) {
				return world;
			}
			return loadedWorlds.getLive().get(name);
		} else {
			return StringUtil.getShortest(StringUtil.matchName(loadedWorlds.getValues(), name));
		}
	}

	@Override
	public SpoutWorld getWorld(UUID uid) {
		for (SpoutWorld world : loadedWorlds.getValues()) {
			if (world.getUID().equals(uid)) {
				return world;
			}
		}
		return null;
	}

	@Override
	public Collection<World> getWorlds() {
		Collection<World> w = new ArrayList<World>();
		for (SpoutWorld world : loadedWorlds.getValues()) {
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

		// TODO - should include generator (and non-zero seed)
		if (generator == null) {
			generator = defaultGenerator;
		}

		SpoutWorld world = WorldFiles.loadWorld(this, generator, name);

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
		// TODO Auto-generated method stub
	}

	@Override
	public boolean stop() {
		return stop(tr("Spout shutting down", getCommandSource())); // TODO distribute the message differently
	}

	private final AtomicBoolean stopping = new AtomicBoolean();

	@Override
	public boolean stop(final String message) {
		return stop(message, true);
	}

	/**
	 * Used to allow subclasses submit final tasks before stopping the scheduler
	 * @param message
	 * @param stopScheduler
	 * @return
	 */
	protected boolean stop(final String message, boolean stopScheduler) {
		final SpoutEngine engine = this;

		if (!stopping.compareAndSet(false, true)) {
			return false;
		}

		getPluginManager().clearPlugins();

		Runnable lastTickTask = new Runnable() {
			@Override
			public void run() {
				setupComplete.set(false);
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
			}
		};
		scheduler.submitLastTickTask(lastTickTask);
		scheduler.submitFinalTask(finalTask, true);
		if (stopScheduler) {
			scheduler.stop();
		}
		return true;
	}

	@Override
	public List<File> getWorldFolders() {
		File[] folders = this.getWorldFolder().listFiles((FilenameFilter) DirectoryFileFilter.INSTANCE);
		if (folders == null || folders.length == 0) {
			return new ArrayList<File>();
		}
		List<File> worlds = new ArrayList<File>(folders.length);
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
		return SharedFileSystem.getWorldsDirectory();
	}

	@Override
	public SyncedRootCommand getRootCommand() {
		return rootCommand;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
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
	public SpoutScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public TaskManager getParallelTaskManager() {
		return parallelTaskManager;
	}

	@Override
	public WorldGenerator getDefaultGenerator() {
		return defaultGenerator;
	}

	@Override
	public Protocol getProtocol(SocketAddress socketAddress) {
		Protocol proto = boundProtocols.get(socketAddress);
		if (proto == null) {
			for (Map.Entry<SocketAddress, Protocol> entry : boundProtocols.entrySet()) {
				if (entry.getKey() instanceof InetSocketAddress && socketAddress instanceof InetSocketAddress) {
					InetSocketAddress key = (InetSocketAddress) entry.getKey(), given = (InetSocketAddress) socketAddress;
					if (key.getPort() == given.getPort() && ((given.getAddress() instanceof Inet4Address && key.getAddress().getHostAddress().equals("0.0.0.0"))
							|| (given.getAddress() instanceof Inet6Address && key.getAddress().getHostAddress().equals("::")))) { // TODO: Make sure IPV6 works
						proto = entry.getValue();
						break;
					}
				}
			}
		}

		if (proto == null) {
			throw new SpoutRuntimeException("No protocol for bound address!");
		}
		return proto;
	}

	@Override
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	@Override
	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	@Override
	public boolean debugMode() {
		return arguments.debug;
	}

	@Override
	public Thread getMainThread() {
		return scheduler.getMainThread();
	}

	@Override
	public void finalizeRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSnapshotRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public void copySnapshotRun() {
		snapshotManager.copyAllSnapshots();
		for (Player player : players.get().values()) {
			((SpoutPlayer) player).copySnapshot();
		}
	}

	@Override
	public void startTickRun(int stage, long delta) {
		switch (stage) {
			case 0:
				engineItemMap.save();
				engineBiomeMap.save();
				break;
		}
	}

	@Override
	public int getMaxStage() {
		return 0;
	}

	@Override
	public boolean setDefaultWorld(World world) {
		if (world == null) {
			return false;
		}

		defaultWorld.set(world);
		return true;
	}

	@Override
	public World getDefaultWorld() {
		final Map<String, SpoutWorld> loadedWorlds = this.loadedWorlds.get();

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
	public String getLogFile() {
		return logFile;
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

		boolean success = loadedWorlds.remove(world.getName(), (SpoutWorld) world);
		if (success) {
			if (save) {
				SpoutWorld w = (SpoutWorld) world;
				if (!scheduler.removeAsyncManager(w)) {
					throw new IllegalStateException("Unable to remove world from scheduler when halting was attempted");
				}
				getEventManager().callDelayedEvent(new WorldUnloadEvent(world));
				w.unload(save);
			}
			//Note: Worlds should not allow being saved twice and/or throw exceptions if accessed after unloading
			//      Also, should blank out as much internal world data as possible, in case plugins retain references to unloaded worlds
		}
		return success;
	}

	public EntityManager getExpectedEntityManager(Point point) {
		Region region = point.getWorld().getRegionFromBlock(point);
		return ((SpoutRegion) region).getEntityManager();
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

	@Override
	public List<String> getAllPlayers() {
		ArrayList<String> names = new ArrayList<String>();
		for (Player player : players.getValues()) {
			names.add(player.getName());
		}
		return Collections.unmodifiableList(names);
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
		//TODO Can someone redo this or make it better?
		return StringUtil.matchName(Arrays.<Player>asList(players.getValues().toArray(new Player[players.getValues().size()])), name);
	}

	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutSession<?> session, int viewDistance) {
		SpoutPlayer player = PlayerFiles.loadPlayerData(playerName);
		boolean created = false;
		if (player == null) {
			getLogger().info("First login for " + playerName + ", creating new player data");
			player = new SpoutPlayer(this, playerName, null, viewDistance);
			created = true;
		}
		SpoutPlayer oldPlayer = players.put(playerName, player);

		if (reclamation != null) {
			reclamation.addPlayer();
		}

		if (oldPlayer != null && oldPlayer.getSession() != null) {
			oldPlayer.kick("Login occured from another client");
		}

		final SpoutSceneComponent scene = (SpoutSceneComponent) player.getScene();
		
		//Test for valid old position
		created |= scene.getTransformLive().getPosition().getWorld() == null;
		
		//Connect the player and set their transform to the default world's spawn.
		player.connect(session, created ? getDefaultWorld().getSpawnPoint() : scene.getTransformLive());

		//Spawn the player in the world
		World world = scene.getTransformLive().getPosition().getWorld();
		world.spawnEntity(player);
		((SpoutWorld) world).addPlayer(player);

		//Set the player to the session
		session.setPlayer(player);

		//Initialize the session
		session.getProtocol().initializeSession(session);
		return player;
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

	protected Collection<SpoutWorld> getLiveWorlds() {
		return loadedWorlds.getLive().values();
	}

	@Override
	public CommandSource getCommandSource() {
		return consoleManager.getCommandSource();
	}

	/**
	 * Gets the item map used across all worlds on the engine
	 * @return engine map
	 */
	public StringMap getEngineItemMap() {
		return engineItemMap;
	}

	/**
	 * Gets the lighting map used across all worlds on the engine
	 * @return engine map
	 */
	public StringMap getEngineLightingMap() {
		return engineLightingMap;
	}

	/**
	 * Gets the biome map used accorss all worlds on the engine
	 * @return biome map
	 */
	public StringMap getBiomeMap() {
		return engineBiomeMap;
	}

	public boolean isSetupComplete() {
		return setupComplete.get();
	}

	// The engine doesn't do any of these

	@Override
	public void runPhysics(int sequence) {
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return SpoutScheduler.END_OF_THE_WORLD;
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) {
	}

	@Override
	public void runLighting(int sequence) {
	}

	@Override
	public CompletionManager getCompletionManager() {
		return completions;
	}

	public DefaultPermissions getDefaultPermissions() {
		return defaultPerms;
	}

	@Override
	public ChatChannelFactory getChatChannelFactory() {
		return chatChannelFactory;
	}

	@Override
	public void setChatChannelFactory(ChatChannelFactory factory) {
		this.chatChannelFactory = factory;
	}

	private class SessionTask implements Runnable {
		final SpoutSessionRegistry registry;

		SessionTask(SpoutSessionRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void run() {
			registry.pulse();
		}
	}

	private Thread executionThread;

	public Thread getExecutionThread() {
		return executionThread;
	}

	public void setExecutionThread(Thread t) {
		this.executionThread = t;
	}

	public int getSequence() {
		return 0;
	}
}
