/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import org.spout.api.ChatColor;
import org.spout.api.Engine;
import org.spout.api.command.Command;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.RootCommand;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Entity;
import org.spout.api.event.EventManager;
import org.spout.api.event.SimpleEventManager;
import org.spout.api.event.server.PreCommandEvent;
import org.spout.api.event.world.WorldLoadEvent;
import org.spout.api.event.world.WorldUnloadEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.CommandUsageException;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.exception.WrappedCommandException;
import org.spout.api.generator.EmptyWorldGenerator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.CommonRecipeManager;
import org.spout.api.inventory.RecipeManager;
import org.spout.api.player.Player;
import org.spout.api.plugin.CommonPluginLoader;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.CommonServiceManager;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginManager;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.plugin.security.CommonSecurityManager;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;

import org.spout.engine.command.AdministrationCommands;
import org.spout.engine.command.MessagingCommands;
import org.spout.engine.command.TestCommands;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.protocol.SpoutSessionRegistry;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.ConsoleManager;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableReference;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

import com.beust.jcommander.Parameter;

public class SpoutEngine extends AsyncManager implements Engine {
	public static final Logger logger = Logger.getLogger("Spout");

	private final String name = "Spout Engine";
	private final File pluginDirectory = FileSystem.PLUGIN_DIRECTORY;
	private final File configDirectory = FileSystem.CONFIG_DIRECTORY;
	private final File updateDirectory = FileSystem.UPDATE_DIRECTORY;
	private final File dataDirectory = FileSystem.DATA_DIRECTORY;
	private final Random random = new Random();
	private final CommonSecurityManager securityManager = new CommonSecurityManager(0); //TODO Need to integrate this/evaluate security in the engine.
	private final CommonPluginManager pluginManager = new CommonPluginManager(this, securityManager, 0.0);
	private final ConsoleManager consoleManager;
	private final EntityManager entityManager = new EntityManager();
	private final EventManager eventManager = new SimpleEventManager();
	private final RecipeManager recipeManager = new CommonRecipeManager();
	private final ServiceManager serviceManager = CommonServiceManager.getInstance();
	private final SnapshotManager snapshotManager = new SnapshotManager();
	private final SnapshotableLinkedHashMap<String, SpoutPlayer> onlinePlayers = new SnapshotableLinkedHashMap<String, SpoutPlayer>(snapshotManager);
	private final RootCommand rootCommand = new RootCommand(this);
	private final WorldGenerator defaultGenerator = new EmptyWorldGenerator();

	private volatile int maxPlayers = 20;
	private volatile String[] allAddresses;

	//The network executor service - Netty dispatches events to this thread
	protected final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutEngine"));
	protected final SpoutSessionRegistry sessions = new SpoutSessionRegistry();
	protected final SpoutScheduler scheduler = new SpoutScheduler(this);
	protected final ConcurrentMap<SocketAddress, BootstrapProtocol> bootstrapProtocols = new ConcurrentHashMap<SocketAddress, BootstrapProtocol>();
	protected final ChannelGroup group = new DefaultChannelGroup();
	protected SpoutConfiguration config = new SpoutConfiguration();
	private File worldFolder = new File(".");
	private SnapshotableLinkedHashMap<String, SpoutWorld> loadedWorlds = new SnapshotableLinkedHashMap<String, SpoutWorld>(snapshotManager);
	private SnapshotableReference<World> defaultWorld = new SnapshotableReference<World>(snapshotManager, null);
	private SpoutPlayer[] emptyPlayerArray = new SpoutPlayer[0];
	private String logFile;
	
	@Parameter(names = "-debug", description="Debug Mode")
	private boolean debugMode = false;

	public SpoutEngine() {
		super(1, new ThreadAsyncExecutor());
		logFile = "logs" + File.separator + "log-%D.txt";
		consoleManager = new ConsoleManager(this);
	}

	public void init(String[] args) {
		
		registerWithScheduler(scheduler);
		if (!getExecutor().startExecutor()) {
			throw new IllegalStateException("SpoutEngine's executor was already started");
		}
	}

	public void start() {
		if (debugMode()) {
			getLogger().warning("Spout has been started in Debug Mode!  This mode is for developers only");
		}

		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());

		// Register commands
		getRootCommand().addSubCommands(this, AdministrationCommands.class, commandRegFactory);
		getRootCommand().addSubCommands(this, MessagingCommands.class, commandRegFactory);
		if (debugMode) {
			getRootCommand().addSubCommands(this, TestCommands.class, commandRegFactory);
		}

		try {
			config.load();
		} catch (ConfigurationException e) {
			getLogger().log(Level.SEVERE, "Error loading config: {0}", e);
		}
		consoleManager.setupConsole(SpoutConfiguration.CONSOLE_TYPE.getString());

		// Start loading plugins
		loadPlugins();
		enablePlugins();
		//At least one plugin should have registered atleast one world
		if (loadedWorlds.getLive().size() == 0) {
			throw new IllegalStateException("There are no loaded worlds!  You must install a plugin that creates a world (Did you forget Vanilla?)");
		}
		//If we don't have a default world set, just grab one.
		getDefaultWorld();

		if (bootstrapProtocols.size() == 0) {
			getLogger().warning("No bootstrap protocols registered! Clients will not be able to connect to the server.");
		}

		scheduler.startMainThread();
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
				logger.log(Level.SEVERE, "Error loading {0}: {1}", new Object[]{plugin.getDescription().getName(), ex.getMessage()});
				ex.printStackTrace();
			}
		}
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
		return onlinePlayers.toArray(emptyPlayerArray);
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public String getAddress() {
		return SpoutConfiguration.ADDRESS.getString();
	}

	@Override
	public String[] getAllAddresses() {
		return allAddresses;
	}

	@Override
	public void broadcastMessage(String message) {
		for (Player player : getOnlinePlayers()) {
			player.sendMessage(message);
		}
		consoleManager.getCommandSource().sendMessage(message);
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
	public void processCommand(CommandSource source, String commandLine) {
		try {
			PreCommandEvent event = getEventManager().callEvent(new PreCommandEvent(source, commandLine));
			if (event.isCancelled()) {
				return;
			}
			commandLine = event.getMessage();
			getRootCommand().execute(source, commandLine.split(" "), -1, false);
		} catch (WrappedCommandException e) {
			if (e.getCause() instanceof NumberFormatException) {
				source.sendMessage(ChatColor.RED + "Number expected; string given!");
			} else {
				source.sendMessage(ChatColor.RED + "Internal error executing command!");
				source.sendMessage(ChatColor.RED + "Error: " + e.getMessage() + "; See console for details.");
				e.printStackTrace();
			}
		} catch (CommandUsageException e) {
			source.sendMessage(ChatColor.RED + e.getMessage());
			source.sendMessage(ChatColor.RED + e.getUsage());
		} catch (CommandException e) {
			// TODO: Better exception handling!
			source.sendMessage(ChatColor.RED + e.getMessage());
		}
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
		return dataDirectory;
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
		} else {
			int shortestMatch = Integer.MAX_VALUE;
			Player shortestPlayer = null;
			for (Player player : onlinePlayers.getValues()) {
				if (player.getName().toLowerCase().startsWith(name)) {
					if (player.getName().length() < shortestMatch) {
						shortestMatch = player.getName().length();
						shortestPlayer = player;
					}
				}
			}
			return shortestPlayer;
		}
		return null;
	}

	@Override
	public Collection<Player> matchPlayer(String name) {
		List<Player> result = new ArrayList<Player>();
		for (Player player : getOnlinePlayers()) {
			if (player.getName().startsWith(name)) {
				result.add(player);
			}
		}
		return result;
	}

	@Override
	public World getWorld(String name) {
		World world = loadedWorlds.get().get(name);
		if (world == null) {
			return loadedWorlds.getLive().get(name);
		} else {
			return world;
		}
	}

	@Override
	public World getWorld(UUID uid) {
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

		SpoutWorld world = WorldFiles.loadWorldData(this, name, generator);
		if(world == null) {
			System.out.println("Creating new world");
			world = new SpoutWorld(name, this, random.nextLong(), generator);
			WorldFiles.saveWorldData(world);
		}
		World oldWorld = loadedWorlds.putIfAbsent(name, world);

		if (oldWorld != null) {
			return oldWorld;
		} else {
			if (!world.getExecutor().startExecutor()) {
				throw new IllegalStateException("Unable to start executor for new world");
			}
			getEventManager().callDelayedEvent(new WorldLoadEvent(world));
			world.start();
			return world;
		}
	}

	@Override
	public void save(boolean worlds, boolean players) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		stop("Spout shutting down");
	}

	@Override
	public void stop(String message) {
		for (SpoutPlayer player : getOnlinePlayers()) {
			player.kick(message);
		}

		for (SpoutWorld world : this.getLiveWorlds()) {
			world.unload(true);
		}

		getPluginManager().clearPlugins();
		consoleManager.stop();
		scheduler.stop();
		group.close();
		bootstrapProtocols.clear();
		executor.shutdown();
	}

	@Override
	public File getWorldFolder() {
		return worldFolder;
	}

	@Override
	public Command getRootCommand() {
		return rootCommand;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public Platform getPlatform() {
		return Platform.SERVER;
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
	public WorldGenerator getDefaultGenerator() {
		return defaultGenerator;
	}

	@Override
	public BootstrapProtocol getBootstrapProtocol(SocketAddress socketAddress) {
		BootstrapProtocol proto = bootstrapProtocols.get(socketAddress);
		if (proto == null) {
			for (Map.Entry<SocketAddress, BootstrapProtocol> entry : bootstrapProtocols.entrySet()) {
				if (entry.getKey() instanceof InetSocketAddress && socketAddress instanceof InetSocketAddress) {
					InetSocketAddress key = (InetSocketAddress) entry.getKey(), given = (InetSocketAddress) socketAddress;
					if (key.getPort() == given.getPort() && (key.getAddress().getHostAddress().equals("0.0.0.0") && given.getAddress() instanceof Inet4Address || given.getAddress() instanceof Inet6Address && key.getAddress().getHostAddress().equals("::"))) { // TODO: Make sure IPV6 works
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
		return debugMode;
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
			((SpoutPlayer) player).copyToSnapshot();
		}
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0:
				sessions.pulse();
				break;
		}
	}

	@Override
	public void haltRun() throws InterruptedException {
		logger.info("Server halting");
	}

	@Override
	public boolean setDefaultWorld(World world) {
		if (world == null) {
			return false;
		} else {
			defaultWorld.set(world);
			return true;
		}
	}

	@Override
	public World getDefaultWorld() {
		World d = defaultWorld.get();
		if (d == null || !loadedWorlds.get().containsKey(d.getName())) {
			Map<String, SpoutWorld> l = loadedWorlds.get();
			if (l.size() == 0) {
				return null;
			} else {
				World first = l.values().iterator().next();
				return first;
			}
		} else {
			return d;
		}
	}

	@Override
	public File getConfigDirectory() {
		return configDirectory;
	}

	@Override
	public String getLogFile() {
		return logFile;
	}

	@Override
	public String[] getAllCommands() {
		Set<String> result = getRootCommand().getChildNames();
		return result.toArray(new String[result.size()]);
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		return unloadWorld(loadedWorlds.getLive().get(name), save);
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		if (world == null) {
			return false;
		} else {
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
	}

	public EntityManager getExpectedEntityManager(Point point) {
		Region region = point.getWorld().getRegionFromBlock(point);
		return ((SpoutRegion) region).getEntityManager();
	}

	public EntityManager getExpectedEntityManager(World world) {
		return ((SpoutWorld) world).getEntityManager();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutSession session) {
		SpoutPlayer player = null;

		// The new player needs a corresponding entity
		Entity newEntity = new SpoutEntity(this, getDefaultWorld().getSpawnPoint(), null);

		boolean success = false;

		while (!success) {
			player = onlinePlayers.getLive().get(playerName);

			if (player != null) {
				if (!player.connect(session, newEntity)) {
					return null;
				} else {
					success = true;
				}
			} else {
				player = new SpoutPlayer(playerName, newEntity, session);
				if (onlinePlayers.putIfAbsent(playerName, player) == null) {
					success = true;
				}
			}
		}

		World world = newEntity.getWorld();
		world.spawnEntity(newEntity);
		session.setPlayer(player);
		((SpoutWorld) world).addPlayer(player);
		return player;
	}

	protected Collection<SpoutWorld> getLiveWorlds() {
		return loadedWorlds.getLive().values();
	}
}
