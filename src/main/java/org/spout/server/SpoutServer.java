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
package org.spout.server;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

import org.spout.api.ChatColor;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.basic.generator.EmptyWorldGenerator;
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
import org.spout.api.exception.CommandException;
import org.spout.api.exception.CommandUsageException;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.exception.WrappedCommandException;
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
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.util.config.Configuration;
import org.spout.server.command.AdministrationCommands;
import org.spout.server.command.MessagingCommands;
import org.spout.server.entity.EntityManager;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.io.StorageQueue;
import org.spout.server.net.SpoutSession;
import org.spout.server.net.SpoutSessionRegistry;
import org.spout.server.player.SpoutPlayer;
import org.spout.server.scheduler.SpoutScheduler;
import org.spout.server.util.thread.AsyncManager;
import org.spout.server.util.thread.ThreadAsyncExecutor;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.server.util.thread.snapshotable.SnapshotableReference;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class SpoutServer extends AsyncManager implements Server {

	private volatile int maxPlayers = 20;

	private volatile String primaryAddress = "0.0.0.0";

	private volatile String[] allAddresses;

	private final File pluginDirectory = new File("plugins");

	private final File configDirectory = new File("config");

	private final File updateDirectory = new File("update");
	
	private final File dataDirectory = new File("data");

	private String logFile = "logs/log-%D.txt";

	private String name = "Spout Server";

	private EntityManager entityManager = new EntityManager();

	private SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * Default world generator
	 */
	private final WorldGenerator defaultGenerator = new EmptyWorldGenerator();

	/**
	 * Online player list
	 */
	private final SnapshotableLinkedHashMap<String, Player> players = new SnapshotableLinkedHashMap<String, Player>(snapshotManager);

	/**
	 * The security manager TODO - need to integrate this
	 */
	private CommonSecurityManager securityManager = new CommonSecurityManager(0);

	/**
	 * The plugin manager for the server
	 */
	private CommonPluginManager pluginManager = new CommonPluginManager(this, securityManager, 0.0);

	/**
	 * The console manager of this server.
	 */
	private final ConsoleManager consoleManager = new ConsoleManager(this, "jline");

	/**
	 * The logger for this class.
	 */
	public static final Logger logger = Logger.getLogger("Minecraft");

	/**
	 * A group containing all of the channels.
	 */
	private final ChannelGroup group = new DefaultChannelGroup();

	/**
	 * The network executor service - Netty dispatches events to this thread
	 * pool.
	 */
	private final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * A list of all the active {@link SpoutSession}s.
	 */
	private final SpoutSessionRegistry sessions = new SpoutSessionRegistry();

	public static final StorageQueue storeQueue = new StorageQueue();

	/**
	 * The scheduler for the server.
	 */
	private final SpoutScheduler scheduler = new SpoutScheduler(this);

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
	 * A list of all players who can not log onto this server.
	 */
	private List<String> bannedPlayers = new ArrayList<String>();

	/**
	 * A list of all operators.
	 */
	private List<String> operators = new ArrayList<String>();

	/**
	 * A folder that holds all of the world data folders inside of it. By
	 * default, it does not exist ('.'), meant for organizational purposes.
	 */
	private File worldFolder = new File(".");

	/**
	 * loaded plugins
	 */
	private SnapshotableLinkedHashMap<String, SpoutWorld> loadedWorlds = new SnapshotableLinkedHashMap<String, SpoutWorld>(snapshotManager);

	private SnapshotableReference<World> defaultWorld = new SnapshotableReference<World>(snapshotManager, null);

	/**
	 * The root commnd for this server.
	 */
	private final RootCommand rootCommand = new RootCommand(this);

	/**
	 * The event manager.
	 */
	private final EventManager eventManager = new SimpleEventManager();

	/**
	 * The service manager.
	 */
	private final ServiceManager serviceManager = CommonServiceManager.getInstance();
	
	/**
	 * The recipe manager.
	 */
	private final RecipeManager recipeManager = new CommonRecipeManager();

	private final ConcurrentMap<SocketAddress, BootstrapProtocol> bootstrapProtocols = new ConcurrentHashMap<SocketAddress, BootstrapProtocol>();
	
	private final Random random = new Random();

	/**
	 * Cached copy of the server configuration, can be used instead of
	 * re-parsing the config file for each access
	 */
	private Configuration configCache = null;
	
	public SpoutServer() {
		super(1, new ThreadAsyncExecutor());
		registerWithScheduler(scheduler);
		init();
		if (!getExecutor().startExecutor()) {
			throw new IllegalStateException("SpoutServer's executor was already started");
		}
	}

	public static void main(String[] args) {

		SpoutServer server = new SpoutServer();
		server.start();

	}

	public void start() {
		Spout.setGame(this);

		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());

		// Register commands
		getRootCommand().addSubCommands(this, AdministrationCommands.class, commandRegFactory);
		getRootCommand().addSubCommands(this, MessagingCommands.class, commandRegFactory);

		consoleManager.setupConsole();

		try {
			loadConfig();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse config", t);
		}

		// Start loading plugins
		loadPlugins();
		enablePlugins();
		//At least one plugin should have registered atleast one world
		if (loadedWorlds.getLive().size() == 0) {
			throw new IllegalStateException("There are no loaded worlds!  You must install a plugin that creates a world");
		}
		//If we don't have a default world set, just grab one.
		getDefaultWorld();
		if (bootstrapProtocols.size() == 0) {
			getLogger().warning("No bootstrap protocols registered! Clients will not be able to connect to the server.");
		}

		getEventManager().registerEvents(new InternalEventListener(this), this);
		scheduler.startMainThread();
	}

	@SuppressWarnings("unchecked")
	private void loadConfig() throws IOException {
		Configuration config = getConfiguration(true);
		boolean save = false;
		try {
			Map<String, String> generators = (Map<String, String>) config.getProperty("worlds");
			if (generators == null) {
				generators = new HashMap<String, String>();
				generators.put("world", "default");
				config.setProperty("worlds", generators);
				save = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read world generators from the config file!");
		}
		try {
			List<String> whitelist = (List<String>) config.getProperty("whitelist");
			if (whitelist == null) {
				whitelist = new ArrayList<String>();
				whitelist.add("Notch");
				whitelist.add("ez");
				whitelist.add("jeb");
				config.setProperty("whitelist", whitelist);
				save = true;
			}
			whitelistedPlayers = whitelist;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read whitelist from the config file!");
		}
		try {
			List<String> bannedList = (List<String>) config.getProperty("banlist");
			if (bannedList == null) {
				bannedList = new ArrayList<String>();
				bannedList.add("satan");
				config.setProperty("banlist", bannedList);
				save = true;
			}
			bannedPlayers = bannedList;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read banlist from the config file!");
		}
		try {
			String fly = config.getString("allowflight");
			if (fly == null) {
				allowFlight = false;
				config.setProperty("allowflight", false);
				save = true;
			} else {
				allowFlight = fly.equalsIgnoreCase("true");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read flight permissions from the config file!");
		}
		try {
			String whitelist = config.getString("usewhitelist");
			if (whitelist == null) {
				this.whitelist = false;
				config.setProperty("usewhitelist", false);
				save = true;
			} else {
				this.whitelist = whitelist.equalsIgnoreCase("true");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read whitelist value from the config file!");
		}
		try {
			String worldFolder = config.getString("worldcontainer");
			if (worldFolder == null) {
				config.setProperty("worldcontainer", ".");
				save = true;
			} else {
				this.worldFolder = new File(worldFolder);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read world container value from the config file!");
		}
		try {
			List<String> opsList = (List<String>) config.getProperty("ops");
			if (opsList == null) {
				opsList = new ArrayList<String>();
				opsList.add("Notch");
				opsList.add("ez");
				opsList.add("jeb");
				config.setProperty("ops", opsList);
				save = true;
			}
			operators = opsList;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read operators from the config file!");
		}
		try {
			String address = config.getString("address");
			if (address == null) {
				config.setProperty("address", "0.0.0.0:25565");
				save = true;
			} else {
				primaryAddress = address;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read server address from the config file!");
		}
		if (save) {
			config.save();
		}
	}

	public void init() {
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);
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
				logger.log(Level.SEVERE, "Error loading {0}: {1}", new Object[] {plugin.getDescription().getName(), ex.getMessage()});
				ex.printStackTrace();
			}
		}
	}

	private void enablePlugins() {
		for (Plugin plugin : pluginManager.getPlugins()) {
			pluginManager.enablePlugin(plugin);
		}

	}

	public Collection<Player> rawGetAllOnlinePlayers() {
		return players.get().values();
	}

	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ServerBootstrap bootstrap = new ServerBootstrap();

	/**
	 * Binds this server to the specified address.
	 *
	 * @param address The addresss.
	 */
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
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public String getAddress() {
		return primaryAddress;
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
	public void save(boolean worlds, boolean players) {
		// TODO Auto-generated method stub

	}

	/*	@Override
		public boolean registerRecipe(Recipe recipe) {
			// TODO Auto-generated method stub
			return false;
		}*/

	@Override
	public boolean allowFlight() {
		return allowFlight;
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
	public boolean isWhitelist() {
		return whitelist;
	}

	@Override
	public void setWhitelist(boolean whitelist) {
		this.whitelist = whitelist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateWhitelist() {
		try {
			Configuration config = getConfiguration(true);
			List<String> whitelist = (List<String>) config.getProperty("whitelist");
			if (whitelist != null) {
				whitelistedPlayers = whitelist;
			} else {
				whitelistedPlayers = new ArrayList<String>();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read whitelist from the config file!");
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

	@SuppressWarnings("unchecked")
	@Override
	public void whitelist(String player) {
		whitelistedPlayers.add(player);
		try {
			Configuration config = getConfiguration(true);
			List<String> whitelist = (List<String>) config.getProperty("whitelist");
			if (whitelist == null) {
				whitelist = whitelistedPlayers;
			} else {
				whitelist.add(player);
			}
			config.setProperty("whitelist", whitelist);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to write whitelist to the config file!");
		}
	}

	@Override
	public void unWhitelist(String player) {
		whitelistedPlayers.remove(player);
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
					//TODO Save the world, save the cheerleader
				}
				//Note: Worlds should not allow being saved twice and/or throw exceptions if accessed after unloading
				//      Also, should blank out as much internal world data as possible, in case plugins retain references to unloaded worlds
			}
			return success;
		}
	}

	@Override
	public World loadWorld(String name, WorldGenerator generator) {
		// TODO - should include generator (and non-zero seed)
		if (generator == null) {
			generator = getGenerator(name);
		}

		SpoutWorld world = new SpoutWorld(name, this, random.nextLong(), generator);

		World oldWorld = loadedWorlds.putIfAbsent(name, world);
		if (oldWorld != null) {
			return oldWorld;
		} else {
			if (!world.getExecutor().startExecutor()) {
				throw new IllegalStateException("Unable to start executor for new world");
			}
			return world;
		}
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
	public Collection<String> getIPBans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ban(String address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ban(Player player) {
		bannedPlayers.add(player.getName());
		Configuration config;
		try
		{
			config = getConfiguration(true);
			List<String> bannedList = (List<String>) config.getStringList("banlist", null);
			if (bannedList == null) {
				bannedList = new ArrayList<String>();
			}
			if (!bannedList.contains(player.getName()))
				bannedList.add(player.getName());
			config.setProperty("banlist", bannedList);
			config.save();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void unban(String playerName) {
		// TODO Auto-generated method stub
		bannedPlayers.remove(playerName);
		Configuration config;
		try
		{
			config = getConfiguration(true);
			List<String> bannedList = (List<String>) config.getStringList("banlist", null);
			if (bannedList == null) {
				bannedList = new ArrayList<String>();
			}
			if (bannedList.contains(playerName))
				bannedList.remove(playerName);
			config.setProperty("banlist", bannedList);
			config.save();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public List<String> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	private Player[] emptyPlayerArray = new Player[0];

	@Override
	public Player[] getOnlinePlayers() {
		Map<String, Player> playerList = players.get();
		ArrayList<Player> onlinePlayers = new ArrayList<Player>(playerList.size());
		for (Player player : playerList.values()) {
			if (player.isOnline()) {
				onlinePlayers.add(player);
			}
		}
		return onlinePlayers.toArray(emptyPlayerArray);
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
		} else {
			int shortestMatch = Integer.MAX_VALUE;
			Player shortestPlayer = null;
			for (Player player : players.getValues()) {
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
	public Collection<Player> getBannedPlayers() {
		Set<Player> players = new HashSet<Player>();
		for (String name : bannedPlayers) {
			players.add(new SpoutPlayer(name));
		}
		return players;
	}

	@Override
	public Collection<Player> getOps() {
		Set<Player> players = new HashSet<Player>();
		for (String name : operators) {
			players.add(new SpoutPlayer(name));
		}
		return players;
	}

	@Override
	public Session newSession(Channel channel) {
		BootstrapProtocol protocol = getBootstrapProtocol(channel.getLocalAddress());
		return new SpoutSession(this, channel, protocol);
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
	public File getConfigDirectory() {
		return configDirectory;
	}
	
	@Override
	public File getDataFolder() {
		return dataDirectory;
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
	public void stop() {
		stop("Server shutting down");
	}

	public void stop(String message) {
		for (Player player : getOnlinePlayers()) {
			player.kick(message);
		}

		getPluginManager().clearPlugins();

		// And finally kill the console
		consoleManager.stop();
		scheduler.stop();

		group.close();
		bootstrapProtocols.clear();
		bootstrap.getFactory().releaseExternalResources();
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();
		snapshotManager.copyAllSnapshots();
		for (Player player : players.get().values()) {
			((SpoutPlayer) player).copyToSnapshot();
		}
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		sessions.pulse();
	}

	@Override
	public void haltRun() throws InterruptedException {
		logger.info("Server halting");
	}

	public SpoutScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public File getConfigFolder() {
		if (!configDirectory.exists()) {
			configDirectory.mkdirs();
		}
		return configDirectory;
	}

	@Override
	public WorldGenerator getDefaultGenerator() {
		return defaultGenerator;
	}

	public EntityManager getExpectedEntityManager(Point point) {
		Region region = point.getWorld().getRegion(point);
		return ((SpoutRegion) region).getEntityManager();
	}

	public EntityManager getExpectedEntityManager(World world) {
		return ((SpoutWorld) world).getEntityManager();
	}

	@Override
	public void finalizeRun() throws InterruptedException {
		entityManager.finalizeRun();
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutSession session) {
		Player player = null;

		// The new player needs a corresponding entity
		Entity newEntity = new SpoutEntity(this, getDefaultWorld().getSpawnPoint(), null);

		boolean success = false;

		while (!success) {
			player = players.getLive().get(playerName);

			if (player != null) {
				if (!((SpoutPlayer) player).connect(session, newEntity)) {
					return null;
				} else {
					success = true;
				}
			} else {
				player = new SpoutPlayer(playerName, newEntity, session);
				if (players.putIfAbsent(playerName, player) == null) {
					success = true;
				}
			}
		}

		if (player == null) {
			throw new IllegalStateException("Attempting to set session to null player, which shouldn't be possible");
		} else {
			World world = newEntity.getWorld();
			world.spawnEntity(newEntity);
			session.setPlayer(player);
			((SpoutWorld) world).addPlayer(player);
		}
		return player;
	}

	private Configuration getConfiguration() throws IOException {
		return getConfiguration(false);
	}

	private Configuration getConfiguration(boolean update) throws IOException {
		if (configCache != null && !update) {
			return configCache;
		}
		File configFile = new File(getConfigFolder(), "spout.yml");
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		Configuration config = new Configuration(configFile);
		config.load();
		configCache = config;
		return config;
	}

	@SuppressWarnings("unchecked")
	private WorldGenerator getGenerator(String name) {
		try {
			Configuration config = getConfiguration();
			Map<String, String> generators = (Map<String, String>) config.getProperty("worlds");
			if (generators != null) {
				String genName = generators.get(name);
				if (genName != null && genName.equalsIgnoreCase("default")) {
					return getDefaultGenerator();
				}
				if (genName != null) {
					String[] split = genName.split(":", 2);
					String id = split.length > 1 ? split[1] : null;
					Plugin plugin = pluginManager.getPlugin(split[0]);

					if (plugin == null) {
						getLogger().severe("Could not find generator for world '" + name + "', Plugin '" + split[0] + "' could not be found!");
					} else if (!plugin.isEnabled()) {
						getLogger().severe("Could not find generator for world '" + name + "', Plugin '" + split[0] + "' is not enabled!");
					} else {
						WorldGenerator gen = plugin.getWorldGenerator(name, id);
						if (gen != null) {
							return gen;
						}
					}
				}
			}
		} catch (IOException ignore) {
		}
		return getDefaultGenerator();
	}

	@Override
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	@Override
	public RecipeManager getRecipeManager() {
		return recipeManager;
	}
}
