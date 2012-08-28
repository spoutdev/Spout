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

import static org.spout.api.lang.Translation.tr;
import static org.spout.api.lang.Translation.log;
import java.io.File;
import java.io.FilenameFilter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import org.spout.api.Engine;
import org.spout.api.FileSystem;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.completion.CompletionManager;
import org.spout.api.chat.completion.CompletionManagerImpl;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.SyncedRootCommand;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.EventManager;
import org.spout.api.event.SimpleEventManager;
import org.spout.api.event.server.ServerStopEvent;
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
import org.spout.api.geo.discrete.Transform;
import org.spout.api.inventory.CommonRecipeManager;
import org.spout.api.inventory.RecipeManager;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.material.MaterialRegistry;
import org.spout.api.permissions.DefaultPermissions;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.plugin.CommonPluginLoader;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.CommonServiceManager;
import org.spout.api.plugin.Platform;
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

import org.spout.api.chat.console.Console;
import org.spout.engine.chat.console.ConsoleManager;
import org.spout.engine.chat.console.FileConsole;
import org.spout.engine.chat.console.JLineConsole;
import org.spout.api.chat.console.MultiConsole;
import org.spout.engine.command.AdministrationCommands;
import org.spout.engine.command.ConnectionCommands;
import org.spout.engine.command.InputCommands;
import org.spout.engine.command.MessagingCommands;
import org.spout.engine.command.TestCommands;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.protocol.SpoutSessionRegistry;
import org.spout.engine.protocol.builtin.SpoutProtocol;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.DeadlockMonitor;
import org.spout.engine.util.TicklockMonitor;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableReference;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.WorldSavingThread;

public abstract class SpoutEngine extends AsyncManager implements Engine {
	private static final SpoutPlayer[] EMPTY_PLAYER_ARRAY = new SpoutPlayer[0];
	private static final Logger logger = Logger.getLogger("Spout");
	private final String name = "Spout Engine";
	private final File pluginDirectory = SharedFileSystem.PLUGIN_DIRECTORY;
	private final File configDirectory = SharedFileSystem.CONFIG_DIRECTORY;
	private final File updateDirectory = SharedFileSystem.UPDATE_DIRECTORY;
	private final File dataDirectory = SharedFileSystem.DATA_DIRECTORY;
	private final Random random = new Random();
	private final CommonSecurityManager securityManager = new CommonSecurityManager(0); //TODO Need to integrate this/evaluate security in the engine.
	private final CommonPluginManager pluginManager = new CommonPluginManager(this, securityManager, 0.0);
	private final ConsoleManager consoleManager;
	private final EntityManager entityManager = new EntityManager();
	private final EventManager eventManager = new SimpleEventManager();
	private final RecipeManager recipeManager = new CommonRecipeManager();
	private final ServiceManager serviceManager = CommonServiceManager.getInstance();
	private final SnapshotManager snapshotManager = new SnapshotManager();
	protected final SnapshotableLinkedHashMap<String, SpoutPlayer> onlinePlayers = new SnapshotableLinkedHashMap<String, SpoutPlayer>(snapshotManager);
	private final SyncedRootCommand rootCommand = new SyncedRootCommand(this);
	private final WorldGenerator defaultGenerator = new EmptyWorldGenerator();
	private volatile int maxPlayers = 20;
	protected final SpoutSessionRegistry sessions = new SpoutSessionRegistry();
	protected final SpoutScheduler scheduler = new SpoutScheduler(this);
	protected final SpoutParallelTaskManager parallelTaskManager = new SpoutParallelTaskManager(this);
	protected final ConcurrentMap<SocketAddress, Protocol> boundProtocols = new ConcurrentHashMap<SocketAddress, Protocol>();
	protected final ChannelGroup group = new DefaultChannelGroup();
	private final AtomicBoolean setupComplete = new AtomicBoolean(false);
	private final MemoryLeakThread leakThread = new MemoryLeakThread();
	protected SpoutConfiguration config = new SpoutConfiguration();
	private final CompletionManager completions = new CompletionManagerImpl();
	private File worldFolder = new File(".");
	private SnapshotableLinkedHashMap<String, SpoutWorld> loadedWorlds = new SnapshotableLinkedHashMap<String, SpoutWorld>(snapshotManager);
	private SnapshotableReference<World> defaultWorld = new SnapshotableReference<World>(snapshotManager, null);
	private String logFile;
	private StringMap engineItemMap = null;
	private StringMap engineBiomeMap = null;
	private ConcurrentHashMap<String, String> cvars = new ConcurrentHashMap<String, String>();
	protected FileSystem filesystem;
	private MultiConsole console;
	private SpoutApplication arguments;

	public SpoutEngine() {
		super(1, new ThreadAsyncExecutor("Engine bootstrap thread"));
		logFile = "logs" + File.separator + "log-%D.txt";
		consoleManager = new ConsoleManager(this);
	}

	public void init(SpoutApplication args) {
		this.arguments = args;
		try {
			config.load();
		} catch (ConfigurationException e) {
			log("Error loading config: %0", Level.SEVERE, e.getMessage(), e);
		}

		console = new MultiConsole(new FileConsole(this), new JLineConsole(this));
		consoleManager.setupConsole(console);

		registerWithScheduler(scheduler);

		if (!getExecutor().startExecutor()) {
			throw new IllegalStateException("SpoutEngine's executor was already started");
		}

		DefaultPermissions.addDefaultPermission(STANDARD_BROADCAST_PERMISSION);

		if (debugMode()) {
			new TicklockMonitor().start();
			new DeadlockMonitor().start();
		}
	}

	public abstract void start();

	public void start(boolean checkWorlds) {
		log("Spout is starting in %0-only mode.", getPlatform().name().toLowerCase());
		log("Current version is %0 (Implementing SpoutAPI %1).", Spout.getEngine().getVersion(), Spout.getAPIVersion());
		log("This software is currently in alpha status so components may");
		log("have bugs or not work at all. Please report any issues to");
		log("http://issues.spout.org");

		if (debugMode()) {
			log("Debug Mode has been toggled on!  This mode is intended for developers only", Level.WARNING);
			leakThread.start();
		}

		scheduler.scheduleSyncRepeatingTask(this, new SessionTask(sessions), 50, 50, TaskPriority.CRITICAL);

		final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this));

		// Register commands
		getRootCommand().addSubCommands(this, AdministrationCommands.class, commandRegFactory);
		getRootCommand().addSubCommands(this, MessagingCommands.class, commandRegFactory);
		getRootCommand().addSubCommands(this, ConnectionCommands.class, commandRegFactory);
		InputCommands.setupInputCommands(this, getRootCommand());
		if (arguments.debug) {
			getRootCommand().addSubCommands(this, TestCommands.class, commandRegFactory);
		}
		Protocol.registerProtocol(new SpoutProtocol());

		//Setup the Material Registry
		engineItemMap = MaterialRegistry.setupRegistry();
		//Setup the Biome Registry
		engineBiomeMap = BiomeRegistry.setupRegistry();

		// Start loading plugins
		loadPlugins();
		postPluginLoad();
		enablePlugins();

		if (checkWorlds) {
			//At least one plugin should have registered atleast one world
			if (loadedWorlds.getLive().size() == 0) {
				throw new IllegalStateException("There are no loaded worlds!  You must install a plugin that creates a world (Did you forget Vanilla?)");
			}

			//Pick the default world from the configuration
			World world = this.getWorld(SpoutConfiguration.DEFAULT_WORLD.getString());
			if (world != null) {
				this.setDefaultWorld(world);
			}

			//If we don't have a default world set, just grab one.
			getDefaultWorld();
		}

		scheduler.startMainThread();
		WorldSavingThread.startThread();
		setupComplete.set(true);
	}

	/**
	 * This method is called after {@link #loadPlugins()} but before {@link #enablePlugins()}
	 */
	protected void postPluginLoad() {
	}

	public void loadPlugins() {
		pluginManager.registerPluginLoader(CommonPluginLoader.class);
		pluginManager.clearPlugins();

		if (!pluginDirectory.exists()) {
			pluginDirectory.mkdirs();
		}

		Plugin[] plugins = pluginManager.loadPlugins(pluginDirectory);

		for (Plugin plugin : plugins) {
			try {
				//Technically unsafe.  This should call the security manager
				plugin.onLoad();
			} catch (Exception ex) {
				log("Error loading %0: %1", Level.SEVERE, plugin.getDescription().getName(), ex.getMessage(), ex);
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
		return onlinePlayers.get().values();
	}

	@Override
	public String getName() {
		return name;
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
		if (!updateDirectory.exists()) {
			updateDirectory.mkdirs();
		}
		return updateDirectory;
	}

	@Override
	public File getConfigFolder() {
		if (!configDirectory.exists()) {
			configDirectory.mkdirs();
		}
		return configDirectory;
	}

	@Override
	public File getDataFolder() {
		if (!dataDirectory.exists()) {
			dataDirectory.mkdirs();
		}
		File playerDirectory = new File(dataDirectory.toString() + File.separator + "players");
		if (!playerDirectory.exists()) {
			playerDirectory.mkdirs();
		}
		return dataDirectory;
	}

	@Override
	public File getPluginFolder() {
		if (!pluginDirectory.exists()) {
			pluginDirectory.mkdirs();
		}
		return pluginDirectory;
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

		SpoutWorld world = WorldFiles.loadWorldFromData(name, generator, engineItemMap);
		if (world == null) {
			log("Generating new world named [%0]", name);

			File itemMapFile = new File(new File(SharedFileSystem.WORLDS_DIRECTORY, name), "materials.dat");
			BinaryFileStore itemStore = new BinaryFileStore(itemMapFile);
			StringMap itemMap = new StringMap(engineItemMap, itemStore, 0, Short.MAX_VALUE, name + "ItemMap");

			world = new SpoutWorld(name, this, random.nextLong(), 0L, generator, UUID.randomUUID(), itemMap, null);
			WorldFiles.saveWorldData(world);
		}
		World oldWorld = loadedWorlds.putIfAbsent(name, world);

		if (oldWorld != null) {
			return oldWorld;
		}

		if (!world.getExecutor().startExecutor()) {
			throw new IllegalStateException("Unable to start executor for new world");
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
				group.close();
				WorldSavingThread.finish();
				WorldSavingThread.staticJoin();
			}
		};
		scheduler.submitLastTickTask(lastTickTask);
		scheduler.submitFinalTask(finalTask);
		scheduler.stop(1);
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
		return worldFolder;
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
	public void finalizeRun() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();
		snapshotManager.copyAllSnapshots();
		for (Player player : onlinePlayers.get().values()) {
			((SpoutPlayer) player).copySnapshot();
		}
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0:
				engineItemMap.save();
				engineBiomeMap.save();
				break;
		}
	}

	@Override
	public void haltRun() throws InterruptedException {
		log("Server halting");
		console.close();
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

		final World first = loadedWorlds.values().iterator().next();
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
				if (!w.getExecutor().haltExecutor()) {
					throw new IllegalStateException("Executor was already halted when halting was attempted");
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

	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutSession<?> session, int viewDistance) {
		SpoutPlayer player;
		try {
			player = WorldFiles.loadPlayerData(playerName, session);
		} catch (Exception e) {
			//generate a player at default spawn, since we don't have a player .dat file
			player = new SpoutPlayer(playerName, getDefaultWorld().getSpawnPoint(), session, this, viewDistance);
		}
		//TODO Olloth fix this
		if (player == null) {
			player = new SpoutPlayer(playerName, getDefaultWorld().getSpawnPoint(), session, this, viewDistance);			
		}
		player.connect(session, player.getTransform());		
		World world = player.getWorld();
		session.getProtocol().setPlayerController(player);
		world.spawnEntity(player);
		session.setPlayer(player);
		onlinePlayers.remove(playerName);
		onlinePlayers.putIfAbsent(playerName, player);
		return player;
	}

	protected Collection<SpoutWorld> getLiveWorlds() {
		return loadedWorlds.getLive().values();
	}

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
	 * Gets the biome map used accorss all worlds on the engine
	 * @return biome map
	 */
	public StringMap getBiomeMap() {
		return engineBiomeMap;
	}

	public boolean isSetupComplete() {
		return setupComplete.get();
	}

	@Override
	public FileSystem getFilesystem() {
		return filesystem;
	}

	public MemoryLeakThread getLeakThread() {
		return leakThread;
	}

	public MultiConsole getConsoles() {
		return console;
	}

	// The engine doesn't do any of these

	@Override
	public void runPhysics(int sequence) throws InterruptedException {
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return SpoutScheduler.END_OF_THE_WORLD;
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) throws InterruptedException {
	}

	@Override
	public void runLighting(int sequence) throws InterruptedException {
	}

	@Override
	public void setVariable(String key, String value) {
		cvars.put(key, value);
	}

	@Override
	public String getVariable(String key) {
		return cvars.get(key);
	}

	@Override
	public CompletionManager getCompletionManager() {
		return completions;
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
}
