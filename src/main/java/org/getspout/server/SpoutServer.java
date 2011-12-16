package org.getspout.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.util.permissions.DefaultPermissions;

import org.getspout.server.command.BanCommand;
import org.getspout.server.command.ColorCommand;
import org.getspout.server.command.DeopCommand;
import org.getspout.server.command.GameModeCommand;
import org.getspout.server.command.SpoutCommandMap;
import org.getspout.server.command.HelpCommand;
import org.getspout.server.command.KickCommand;
import org.getspout.server.command.ListCommand;
import org.getspout.server.command.MeCommand;
import org.getspout.server.command.OpCommand;
import org.getspout.server.command.ReloadCommand;
import org.getspout.server.command.SaveCommand;
import org.getspout.server.command.SayCommand;
import org.getspout.server.command.StopCommand;
import org.getspout.server.command.TimeCommand;
import org.getspout.server.command.ToggleStormCommand;
import org.getspout.server.command.WhitelistCommand;
import org.getspout.server.config.SingleFileYamlConfiguration;
import org.getspout.server.inventory.CraftingManager;
import org.getspout.server.io.StorageQueue;
import org.getspout.server.io.mcregion.McRegionWorldStorageProvider;
import org.getspout.server.map.SpoutMapView;
import org.getspout.server.net.MinecraftPipelineFactory;
import org.getspout.server.net.Session;
import org.getspout.server.net.SessionRegistry;
import org.getspout.server.scheduler.SpoutScheduler;
import org.getspout.server.util.DeadlockMonitor;
import org.getspout.server.util.PlayerListFile;
import org.getspout.server.util.bans.BanManager;
import org.getspout.server.util.bans.FlatFileBanManager;

/**
 * The core class of the Spout server.
 * @author Graham Edgecombe
 */
public final class SpoutServer implements Server {
	/**
	 * The logger for this class.
	 */
	public static final Logger logger = Logger.getLogger("Minecraft");

	/**
	 * The directory configurations are stored in
	 */
	private static final File configDir = new File("config");

	/**
	 * The main configuration file
	 */
	private static final File configFile = new File(configDir, "spout.yml");

	/**
	 * The configuration the server uses.
	 */
	private static final SingleFileYamlConfiguration config = new SingleFileYamlConfiguration(configFile);

	/**
	 * The protocol version supported by the server
	 */
	public static final int PROTOCOL_VERSION = 22;


	public static final StorageQueue storeQueue = new StorageQueue();

	/**
	 * Creates a new server on TCP port 25565 and starts listening for
	 * connections.
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			storeQueue.start();

			if (!configDir.exists() || !configDir.isDirectory())
				configDir.mkdirs();
			config.load();
			config.options().indent(4);
			ConfigurationSerialization.registerClass(SpoutOfflinePlayer.class);

			SpoutServer server = new SpoutServer();
			server.start();
			List<String> binds = config.getStringList("server.bind");
			boolean hasBound = false;
			if (binds != null) {
				for (String bind : binds) {
					String[] split = bind.split("@");
					if (split.length != 2) {
						split = bind.split(":");
					}
					if (split.length > 2) continue;
					int port = 25565;
					try {
						if (split.length > 1) {
							port = Integer.parseInt(split[1]);
						}
					} catch (NumberFormatException e) {}
					server.bind(new InetSocketAddress(split[0], port));
					hasBound = true;
				}
			}
			if (!hasBound) {
				server.bind(new InetSocketAddress(config.getInt("server.port", 25565)));
			}
			logger.info("Ready for connections.");
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Error during server startup.", t);
		}
	}

	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ServerBootstrap bootstrap = new ServerBootstrap();

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
	 * A list of all the active {@link Session}s.
	 */
	private final SessionRegistry sessions = new SessionRegistry();

	/**
	 * The console manager of this server.
	 */
	private final ConsoleManager consoleManager = new ConsoleManager(this, config.getString("server.terminal-mode", "jline"));

	/**
	 * The services manager of this server.
	 */
	private final SimpleServicesManager servicesManager = new SimpleServicesManager();

	/**
	 * The command map of this server.
	 */
	private final SpoutCommandMap commandMap = new SpoutCommandMap(this);

	/**
	 * The plugin manager of this server.
	 */
	private final SimplePluginManager pluginManager = new SimplePluginManager(this, commandMap);

	/**
	 * The crafting manager for this server.
	 */
	private final CraftingManager craftingManager = new CraftingManager();

	/**
	 * The list of OPs on the server.
	 */
	private final PlayerListFile opsList = new PlayerListFile(new File(configDir, "ops.txt"));

	/**
	 * The list of players whitelisted on the server.
	 */
	private final PlayerListFile whitelist = new PlayerListFile(new File(configDir, "whitelist.txt"));

	/**
	 * The server's ban manager.
	 */
	private BanManager banManager = new FlatFileBanManager(this);

	/**
	 * The world this server is managing.
	 */
	private final ArrayList<SpoutWorld> worlds = new ArrayList<SpoutWorld>();

	/**
	 * The task scheduler used by this server.
	 */
	private final SpoutScheduler scheduler = new SpoutScheduler(this);

	/**
	 * The server's default game mode
	 */
	private GameMode defaultGameMode = GameMode.SURVIVAL;

	/**
	 * Whether the server is shutting down
	 */
	private boolean isShuttingDown = false;

	/**
	 * A cache of existing OfflinePlayers
	 */
	private final Map<String, OfflinePlayer> offlineCache = new ConcurrentHashMap<String, OfflinePlayer>();

	/**
	 * Deadlock monitor detection thread;
	 */
	DeadlockMonitor monitor;

	/**
	 * Creates a new server.
	 */
	public SpoutServer() {
		init();
	}

	/**
	 * Initializes the channel and pipeline factories.
	 */
	private void init() {
		Bukkit.setServer(this);

		monitor = new DeadlockMonitor();
		//monitor.start();

		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new MinecraftPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);

		// TODO: This needs a cleanup badly
		InputStream stream = getClass().getClassLoader().getResourceAsStream("defaults/spout.yml");
		if (stream == null) {
			logger.severe("Error creating default config: Config not found in classpath");
			return;
		}
		try {
			config.setDefaults(YamlConfiguration.loadConfiguration(stream));
		} finally {
			try {
				stream.close();
			} catch (IOException e) {}
		}
		config.set("server.view-distance", SpoutChunk.VISIBLE_RADIUS);

		// If the configuration is empty, attempt to migrate non-Spout configs
		if (config.getKeys(false).size() <= 1) {
			System.out.println("Generating default configuration config/spout.yml...");

			// bukkit.yml
			File bukkitYml = new File("bukkit.yml");
			if (bukkitYml.exists()) {
				YamlConfiguration bukkit = YamlConfiguration.loadConfiguration(bukkitYml);
				String moved = "", separator = "";

				if (bukkit.get("database") != null) {
					config.createSection("database", bukkit.getConfigurationSection("database").getValues(true));
					moved += separator + "database settings";
					separator = ", ";
				}

				if (bukkit.get("settings.spawn-radius") != null) {
					config.set("server.spawn-radius", bukkit.getInt("settings.spawn-radius", 16));
					moved += separator + "spawn radius";
					separator = ", ";
				}

				if (bukkit.getString("settings.update-folder") != null) {
					config.set("server.folders.update", bukkit.getString("settings.update-folder"));
					moved += separator + "update folder";
					separator = ", ";
				}

				if(bukkit.getString("settings.world-container") != null) {
					config.set("server.folders.world-container", bukkit.getString("settings.world-container"));
					moved += separator + "world container";
					separator = "m ";
				}

				if (bukkit.get("worlds") != null) {
					config.createSection("worlds", bukkit.getConfigurationSection("worlds").getValues(true));
					moved += separator + "world generators";
					separator = ", ";
				}

				// TODO: move aliases when those are implemented

				if (moved.length() > 0) {
					System.out.println("Copied " + moved + " from bukkit.yml");
				}
			}

			// server.properties
			File serverProps = new File("server.properties");
			if (serverProps.exists()) {
				try {
					Properties properties = new Properties();
					properties.load(new FileInputStream(serverProps));
					String moved = "", separator = "";

					if (properties.containsKey("level-name")) {
						String world = properties.getProperty("level-name", "world");
						config.set("server.world-name", world);
						moved += separator + "world name";
						separator = ", ";
					}

					if (properties.containsKey("online-mode")) {
						String value = properties.getProperty("online-mode", "true");
						boolean bool = value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
						config.set("server.online-mode", bool);
						moved += separator + "online mode";
						separator = ", ";
					}

					if (properties.containsKey("server-port")) {
						String value = properties.getProperty("server-port", "25565");
						try {
							int port = Integer.parseInt(value);
							config.set("server.port", port);
							moved += separator + "port";
							separator = ", ";
						}
						catch (NumberFormatException ex) {}
					}

					if (properties.containsKey("max-players")) {
						String value = properties.getProperty("max-players", "20");
						try {
							int players = Integer.parseInt(value);
							config.set("server.max-players", players);
							moved += separator + "max players";
							separator = ", ";
						}
						catch (NumberFormatException e) {}
					}

					if (properties.containsKey("motd")) {
						String motd = properties.getProperty("motd", "Spout server");
						config.set("server.motd", motd);
						moved += separator + "MOTD";
						separator = ", ";
					}

					if (properties.containsKey("gamemode")) {
						String value = properties.getProperty("gamemode", "0");
						try {
							int mode = Integer.parseInt(value);
							GameMode gMode = GameMode.getByValue(mode);
							if (gMode == null) gMode = GameMode.SURVIVAL;
							config.set("server.def-game-mode", gMode.name());
							moved += separator + "default game mode";
							separator = ", ";
						} catch (NumberFormatException ex) {}
					}

					// TODO: move nether, view distance, monsters, etc when implemented

					if (moved.length() > 0) {
						System.out.println("Copied " + moved + " from server.properties");
					}
				}
				catch (IOException ex) {}
			}
		}
		config.options().copyDefaults(true);
		config.save();
	}

	/**
	 * Binds this server to the specified address.
	 * @param address The addresss.
	 */
	public void bind(SocketAddress address) {
		logger.log(Level.INFO, "Binding to address: {0}...", address);
		group.add(bootstrap.bind(address));
	}

	/**
	 * Starts this server.
	 */
	public void start() {
		// Config should have already loaded by this point, but to be safe...
		config.load();
		consoleManager.setupConsole();

		// Load player lists
		opsList.load();
		whitelist.load();
		banManager.load();

		// Start loading plugins
		loadPlugins();

		// Begin registering permissions
		DefaultPermissions.registerCorePermissions();

		// Register these first so they're usable while the worlds are loading
		SpoutCommandMap.initSpoutPermissions(this);
		commandMap.register(new MeCommand(this));
		commandMap.register(new ColorCommand(this));
		commandMap.register(new KickCommand(this));
		commandMap.register(new ListCommand(this));
		commandMap.register(new TimeCommand(this));
		commandMap.register(new WhitelistCommand(this));
		commandMap.register(new BanCommand(this));
		commandMap.register(new GameModeCommand(this));
		commandMap.register(new OpCommand(this));
		commandMap.register(new DeopCommand(this));
		commandMap.register(new StopCommand(this));
		commandMap.register(new SaveCommand(this));
		commandMap.register(new SayCommand(this));
		commandMap.removeAllOfType(ReloadCommand.class);
		commandMap.register(new ReloadCommand(this));
		commandMap.register(new HelpCommand(this, commandMap.getKnownCommands(false)));
		commandMap.register(new ToggleStormCommand(this));

		enablePlugins(PluginLoadOrder.STARTUP);

		// Create worlds
		String world = config.getString("server.world-name", "world");
		createWorld(WorldCreator.name(world).environment(Environment.NORMAL));
		if (getAllowNether()) {
			createWorld(WorldCreator.name(world + "_nether").environment(Environment.NETHER));
		}
		if (getAllowEnd()) {
			createWorld(WorldCreator.name(world + "_the_end").environment(Environment.THE_END));
		}

		// Finish loading plugins
		enablePlugins(PluginLoadOrder.POSTWORLD);
		commandMap.registerServerAliases();
		consoleManager.refreshCommands();
	}

	/**
	 * Stops this server.
	 */
	public void shutdown() {
		// This is so we don't run this twice (/stop and actual shutdown)
		if (isShuttingDown) return;
		isShuttingDown = true;
		logger.info("The server is shutting down...");

		monitor.interrupt();

		// Stop scheduler and disable plugins
		scheduler.stop();
		pluginManager.clearPlugins();

		// Kick (and save) all players
		for (Player player : getOnlinePlayers()) {
			player.kickPlayer("Server shutting down.");
		}

		// Save worlds
		for (World world : getWorlds()) {
			unloadWorld(world, true);
		}
		storeQueue.end();

		// Gracefully stop Netty
		group.close();
		bootstrap.getFactory().releaseExternalResources();

		// And finally kill the console
		consoleManager.stop();

	}

	/**
	 * Loads all plugins, calling onLoad, &c.
	 */
	private void loadPlugins() {
		// clear the map
		commandMap.removeAllOfType(PluginCommand.class);

		File folder = new File(config.getString("server.folders.plugins", "plugins"));
		folder.mkdirs();

		// clear plugins and prepare to load
		pluginManager.clearPlugins();
		pluginManager.registerInterface(JavaPluginLoader.class);
		Plugin[] plugins = pluginManager.loadPlugins(folder);

		// call onLoad methods
		for (Plugin plugin : plugins) {
			try {
				plugin.onLoad();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Error loading {0}: {1}", new Object[]{plugin.getDescription().getName(), ex.getMessage()});
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Enable all plugins of the given load order type.
	 * @param type The type of plugin to enable.
	 */
	public void enablePlugins(PluginLoadOrder type) {
		Plugin[] plugins = pluginManager.getPlugins();
		for (Plugin plugin : plugins) {
			if (!plugin.isEnabled() && plugin.getDescription().getLoad() == type) {
				List<Permission> perms = plugin.getDescription().getPermissions();
				for (Permission perm : perms) {
					try {
						pluginManager.addPermission(perm);
					} catch (IllegalArgumentException ex) {
						getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
					}
				}

				try {
					pluginManager.enablePlugin(plugin);
				} catch (Throwable ex) {
					logger.log(Level.SEVERE, "Error loading {0}", plugin.getDescription().getFullName());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Reloads the server, refreshing settings and plugin information
	 */
	public void reload() {
		try {
			// Reload relevant configuration
			config.load();
			opsList.load();
			whitelist.load();

			// Reset crafting
			craftingManager.resetRecipes();

			// Load plugins
			loadPlugins();
			DefaultPermissions.registerCorePermissions();
			SpoutCommandMap.initSpoutPermissions(this);
			commandMap.registerAllPermissions();
			enablePlugins(PluginLoadOrder.STARTUP);
			enablePlugins(PluginLoadOrder.POSTWORLD);
			commandMap.registerServerAliases();
			consoleManager.refreshCommands();
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Uncaught error while reloading: {0}", ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the channel group.
	 * @return The {@link ChannelGroup}.
	 */
	public ChannelGroup getChannelGroup() {
		return group;
	}

	/**
	 * Gets the session registry.
	 * @return The {@link SessionRegistry}.
	 */
	public SessionRegistry getSessionRegistry() {
		return sessions;
	}

	/**
	 * Returns the list of OPs on this server.
	 */
	public PlayerListFile getOpsList() {
		return opsList;
	}

	/**
	 * Returns the list of OPs on this server.
	 */
	public PlayerListFile getWhitelist() {
		return whitelist;
	}

	/**
	 * Returns the folder where configuration files are stored
	 */
	public File getConfigDir() {
		return configDir;
	}

	public Set<OfflinePlayer> getOperators() {
		Set<OfflinePlayer> offlinePlayers = new HashSet<OfflinePlayer>();
		for (String name : opsList.getContents()) {
			offlinePlayers.add(getOfflinePlayer(name));
		}
		return offlinePlayers;
	}

	/**
	 * Returns the currently used ban manager for the server
	 */
	public BanManager getBanManager() {
		return banManager;
	}

	public void setBanManager(BanManager manager) {
		this.banManager = manager;
		manager.load();
		logger.log(Level.INFO, "Using {0} for ban management", manager.getClass().getName());
	}

	/**
	 * Gets the world by the given name.
	 * @param name The name of the world to look up.
	 * @return The {@link SpoutWorld} this server manages.
	 */
	public SpoutWorld getWorld(String name) {
		for (SpoutWorld world : worlds) {
			if (world.getName().equalsIgnoreCase(name))
				return world;
		}
		return null;
	}

	/**
	 * Gets the world from the given Unique ID
	 *
	 * @param uid Unique ID of the world to retrieve.
	 * @return World with the given Unique ID, or null if none exists.
	 */
	public SpoutWorld getWorld(UUID uid) {
		for (SpoutWorld world : worlds) {
			if (uid.equals(world.getUID()))
				return world;
		}
		return null;
	}

	/**
	 * Gets the list of worlds currently loaded.
	 * @return An ArrayList containing all loaded worlds.
	 */
	public List<World> getWorlds() {
		return new ArrayList<World>(worlds);
	}

	/**
	 * Gets a list of available commands from the command map.
	 * @return A list of all commands at the time.
	 */
	protected String[] getAllCommands() {
		Set<String> knownCommandNames = commandMap.getKnownCommandNames();
		return knownCommandNames.toArray(new String[knownCommandNames.size()]);
	}

	/**
	 * Gets the name of this server implementation
	 *
	 * @return "Spout"
	 */
	public String getName() {
		return "Spout";
	}

	/**
	 * Gets the version string of this server implementation.
	 *
	 * @return version of this server implementation
	 */
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	public String getBukkitVersion() {
		return getClass().getPackage().getSpecificationVersion();
	}

	/**
	 * Gets a list of all currently logged in players
	 *
	 * @return An array of Players that are currently online
	 */
	public Player[] getOnlinePlayers() {
		ArrayList<Player> result = new ArrayList<Player>();
		for (World world : getWorlds()) {
			for (Player player : world.getPlayers())
				result.add(player);
		}
		return result.toArray(new Player[result.size()]);
	}

	/**
	 * Gets every player that has ever played on this server.
	 *
	 * @return Array containing all players
	 */
	public OfflinePlayer[] getOfflinePlayers() {
		Set<OfflinePlayer> result = new HashSet<OfflinePlayer>();
		for (SpoutWorld world : worlds) {
			result.addAll(world.getRawPlayers());
			for (String name : world.getMetadataService().getPlayerNames()) {
				OfflinePlayer offline = getOfflinePlayer(name);
				if (!result.contains(offline)) {
					result.add(offline);
				}
			}
		}
		return result.toArray(new OfflinePlayer[result.size()]);
	}

	/**
	 * Get the maximum amount of players which can login to this server
	 *
	 * @return The amount of players this server allows
	 */
	public int getMaxPlayers() {
		return config.getInt("server.max-players", 50);
	}

	/**
	 * Gets the port the server listens on.
	 * @return The port number the server is listening on.
	 */
	public int getPort() {
		return config.getInt("server.port", 25565);
	}

	/**
	 * Get the IP that this server is bound to or empty string if not specified
	 *
	 * @return The IP string that this server is bound to, otherwise empty string
	 */
	public String getIp() {
		return "";
	}

	/**
	 * Get the name of this server
	 *
	 * @return The name of this server
	 */
	public String getServerName() {
		return "Spout Server";
	}

	/**
	 * Get an ID of this server. The ID is a simple generally alphanumeric
	 * ID that can be used for uniquely identifying this server.
	 *
	 * @return The ID of this server
	 */
	public String getServerId() {
		return Integer.toHexString(getServerName().hashCode());
	}

	public ConsoleCommandSender getConsoleSender() {
		return consoleManager.getSender();
	}

	/**
	 * Broadcast a message to all players.
	 *
	 * @param message the message
	 * @return the number of players
	 */
	public int broadcastMessage(String message) {
		return broadcast(message, BROADCAST_CHANNEL_USERS);
	}

	/**
	 * Gets the name of the update folder. The update folder is used to safely update
	 * plugins at the right moment on a plugin load.
	 *
	 * @return The name of the update folder
	 */
	public String getUpdateFolder() {
		return config.getString("server.folders.update", "update");
	}

	public File getUpdateFolderFile() {
		return new File(getUpdateFolder());
	}

	/**
	 * Gets a player object by the given username
	 *
	 * This method may not return objects for offline players
	 *
	 * @param name Name to look up
	 * @return Player if it was found, otherwise null
	 */
	public Player getPlayer(String name) {
		for (Player player : getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name))
				return player;
		}
		return null;
	}

	public Player getPlayerExact(String name) {
		for (Player player : getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name))
				return player;
		}
		return null;
	}

	/**
	 * Attempts to match any players with the given name, and returns a list
	 * of all possibly matches
	 *
	 * This list is not sorted in any particular order. If an exact match is found,
	 * the returned list will only contain a single result.
	 *
	 * @param name Name to match
	 * @return List of all possible players
	 */
	public List<Player> matchPlayer(String name) {
		ArrayList<Player> result = new ArrayList<Player>();
		for (Player player : getOnlinePlayers()) {
			if (player.getName().startsWith(name)) {
				result.add(player);
			}
		}
		return result;
	}

	/**
	 * Gets the PluginManager for interfacing with plugins
	 *
	 * @return PluginManager for this SpoutServer instance
	 */
	public SimplePluginManager getPluginManager() {
		return pluginManager;
	}

	/**
	 * Gets the Scheduler for managing scheduled events
	 *
	 * @return Scheduler for this SpoutServer instance
	 */
	public SpoutScheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Gets a services manager
	 *
	 * @return Services manager
	 */
	public SimpleServicesManager getServicesManager() {
		return servicesManager;
	}

	/**
	 * Gets the default ChunkGenerator for the given environment.
	 * @return The ChunkGenerator.
	 */
	private ChunkGenerator getGenerator(String name, Environment environment) {
		if (config.getString("worlds." + name + ".generator") != null) {
			String[] args = config.getString("worlds." + name + ".generator").split(":", 2);
			if (getPluginManager().getPlugin(args[0]) == null) {
				logger.log(Level.WARNING, "Plugin {0} specified for world {1} does not exist, using default.", new Object[]{args[0], name});
			} else {
				return getPluginManager().getPlugin(args[0]).getDefaultWorldGenerator(name, args.length == 2 ? args[1] : "");
			}
		}

		if (environment == Environment.NETHER) {
			return new org.getspout.server.generator.UndergroundGenerator();
		} else if (environment == Environment.THE_END) {
			return new org.getspout.server.generator.CakeTownGenerator();
		} else {
			return new org.getspout.server.generator.SurfaceGenerator();
		}
	}

	/**
	 * Creates or loads a world with the given name.
	 * If the world is already loaded, it will just return the equivalent of
	 * getWorld(name)
	 *
	 * @param name Name of the world to load
	 * @param environment Environment type of the world
	 * @return Newly created or loaded World
	 */
	@Deprecated
	public SpoutWorld createWorld(String name, Environment environment) {
		return createWorld(WorldCreator.name(name).environment(environment));
	}

	/**
	 * Creates or loads a world with the given name.
	 * If the world is already loaded, it will just return the equivalent of
	 * getWorld(name)
	 *
	 * @param name Name of the world to load
	 * @param environment Environment type of the world
	 * @param seed Seed value to create the world with
	 * @return Newly created or loaded World
	 */
	@Deprecated
	public SpoutWorld createWorld(String name, Environment environment, long seed) {
		return createWorld(WorldCreator.name(name).environment(environment).seed(seed));
	}

	/**
	 * Creates or loads a world with the given name.
	 * If the world is already loaded, it will just return the equivalent of
	 * getWorld(name)
	 *
	 * @param name Name of the world to load
	 * @param environment Environment type of the world
	 * @param generator ChunkGenerator to use in the construction of the new world
	 * @return Newly created or loaded World
	 */
	@Deprecated
	public SpoutWorld createWorld(String name, Environment environment, ChunkGenerator generator) {
		return createWorld(WorldCreator.name(name).environment(environment).generator(generator));
	}

	/**
	 * Creates or loads a world with the given name.
	 * If the world is already loaded, it will just return the equivalent of
	 * getWorld(name)
	 *
	 * @param name Name of the world to load
	 * @param environment Environment type of the world
	 * @param seed Seed value to create the world with
	 * @param generator ChunkGenerator to use in the construction of the new world
	 * @return Newly created or loaded World
	 */
	@Deprecated
	public SpoutWorld createWorld(String name, Environment environment, long seed, ChunkGenerator generator) {
		return createWorld(WorldCreator.name(name).environment(environment).seed(seed).generator(generator));
	}

	/**
	 * Creates or loads a world with the given name using the specified options.
	 * <p>
	 * If the world is already loaded, it will just return the equivalent of
	 * getWorld(creator.name()).
	 *
	 * @param creator Options to use when creating the world
	 * @return Newly created or loaded world
	 */
	public SpoutWorld createWorld(WorldCreator creator) {
		SpoutWorld world = getWorld(creator.name());
		if (world != null) {
			return world;
		}

		if (creator.generator() == null) {
			creator.generator(getGenerator(creator.name(), creator.environment()));
		}

		world = new SpoutWorld(this, creator.name(), creator.environment(), creator.seed(), new McRegionWorldStorageProvider(new File(getWorldContainer(), creator.name())), creator.generator());
		worlds.add(world);
		return world;
	}

	/**
	 * Unloads a world with the given name.
	 *
	 * @param name Name of the world to unload
	 * @param save Whether to save the chunks before unloading.
	 * @return Whether the action was Successful
	 */
	public boolean unloadWorld(String name, boolean save) {
		SpoutWorld world = getWorld(name);
		if (world == null) return false;
		return unloadWorld(world, save);
	}

	/**
	 * Unloads the given world.
	 *
	 * @param world The world to unload
	 * @param save Whether to save the chunks before unloading.
	 * @return Whether the action was Successful
	 */
	public boolean unloadWorld(World world, boolean save) {
		if (!(world instanceof SpoutWorld)) {
			return false;
		}
		if (save) {
			world.setAutoSave(false);
			((SpoutWorld) world).save(false);
		}
		if (worlds.contains((SpoutWorld) world)) {
			worlds.remove((SpoutWorld) world);
			((SpoutWorld) world).unload();
			EventFactory.onWorldUnload((SpoutWorld)world);
			return true;
		}
		return false;
	}

	/**
	 * Returns the primary logger associated with this server instance
	 *
	 * @return Logger associated with this server
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Gets a {@link PluginCommand} with the given name or alias
	 *
	 * @param name Name of the command to retrieve
	 * @return PluginCommand if found, otherwise null
	 */
	public PluginCommand getPluginCommand(String name) {
		Command command = commandMap.getCommand(name);
		if (command instanceof PluginCommand) {
			return (PluginCommand) command;
		} else {
			return null;
		}
	}

	/**
	 * Writes loaded players to disk
	 */
	public void savePlayers() {
		for (Player player : getOnlinePlayers())
			player.saveData();
	}

	/**
	 * Dispatches a command on the server, and executes it if found.
	 *
	 * @param commandLine command + arguments. Example: "test abc 123"
	 * @return targetFound returns false if no target is found.
	 * @throws CommandException Thrown when the executor for the given command fails with an unhandled exception
	 */
	public boolean dispatchCommand(CommandSender sender, String commandLine) {
		try {
			if (commandMap.dispatch(sender, commandLine, false)) {
				return true;
			}

			if (getFuzzyCommandMatching()) {
				if (commandMap.dispatch(sender, commandLine, true)) {
					return true;
				}
			}

			return false;
		}
		catch (CommandException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new CommandException("Unhandled exception executing command", ex);
		}
	}

	/**
	 * Populates a given {@link com.avaje.ebean.config.ServerConfig} with values attributes to this server
	 *
	 * @param dbConfig ServerConfig to populate
	 */
	public void configureDbConfig(com.avaje.ebean.config.ServerConfig dbConfig) {
		com.avaje.ebean.config.DataSourceConfig ds = new com.avaje.ebean.config.DataSourceConfig();
		ds.setDriver(config.getString("database.driver", "org.sqlite.JDBC"));
		ds.setUrl(config.getString("database.url", "jdbc:sqlite:{DIR}{NAME}.db"));
		ds.setUsername(config.getString("database.username", "spout"));
		ds.setPassword(config.getString("database.password", "unleashtheflow"));
		ds.setIsolationLevel(com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation.getLevel(config.getString("database.isolation", "SERIALIZABLE")));

		if (ds.getDriver().contains("sqlite")) {
			dbConfig.setDatabasePlatform(new com.avaje.ebean.config.dbplatform.SQLitePlatform());
			dbConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
		}

		dbConfig.setDataSourceConfig(ds);
	}

	/**
	 * Return the crafting manager.
	 * @return The server's crafting manager.
	 */
	public CraftingManager getCraftingManager() {
		return craftingManager;
	}

	/**
	 * Adds a recipe to the crafting manager.
	 * @param recipe The recipe to add.
	 * @return True to indicate that the recipe was added.
	 */
	public boolean addRecipe(Recipe recipe) {
		return craftingManager.addRecipe(recipe);
	}

	public Map<String, String[]> getCommandAliases() {
		Map<String, String[]> aliases = new HashMap<String, String[]>();
		ConfigurationSection section = config.getConfigurationSection("aliases");
		if (section == null) return aliases;
		List<String> cmdAliases = new ArrayList<String>();
		for (String key : section.getKeys(false)) {
			cmdAliases.clear();
			cmdAliases.addAll(section.getStringList(key));
			aliases.put(key, cmdAliases.toArray(new String[cmdAliases.size()]));
		}
		return aliases;
	}

	public void reloadCommandAliases() {
		commandMap.removeAllOfType(MultipleCommandAlias.class);
		commandMap.registerServerAliases();
	}

	public int getSpawnRadius() {
		return config.getInt("server.spawn-radius", 16);
	}

	public void setSpawnRadius(int value) {
		config.set("server.spawn-radius", value);
	}

	public boolean getOnlineMode() {
		return config.getBoolean("server.online-mode", true);
	}

	public boolean getAllowNether() {
		return config.getBoolean("server.allow-nether", true);
	}

	public boolean getAllowEnd() {
		return config.getBoolean("server.allow-end", true);
	}

	public boolean hasWhitelist() {
		return config.getBoolean("server.whitelist", false);
	}

	public void setWhitelist(boolean enabled) {
		config.set("server.whitelist", enabled);
	}

	public Set<OfflinePlayer> getWhitelistedPlayers() {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		for (String name : whitelist.getContents()) {
			players.add(getOfflinePlayer(name));
		}
		return players;
	 }

	public void reloadWhitelist() {
		whitelist.load();
	}

	public boolean getAllowFlight() {
		return config.getBoolean("server.allow-flight", false);
	}

	public int broadcast(String message, String permission) {
		int count = 0;
		for (Permissible permissible : getPluginManager().getPermissionSubscriptions(permission)) {
			if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
				((CommandSender) permissible).sendMessage(message);
				++count;
			}
		}
		return count;
	}

	public OfflinePlayer getOfflinePlayer(String name) {
		OfflinePlayer player = getPlayerExact(name);
		if (player == null) {
			player = offlineCache.get(name);
			if (player == null) {
				player = new SpoutOfflinePlayer(this, name);
				offlineCache.put(name, player);
				// Call creation event here?
			}
		} else {
			offlineCache.remove(name);
		}
		return player;
	}

	public Set<String> getIPBans() {
		return banManager.getIpBans();
	}

	public void banIP(String address) {
	   banManager.setIpBanned(address, true);
	}

	public void unbanIP(String address) {
		banManager.setIpBanned(address, false);
	}

	public Set<OfflinePlayer> getBannedPlayers() {
		Set<OfflinePlayer> bannedPlayers = new HashSet<OfflinePlayer>();
		for (String name : banManager.getBans()) {
			bannedPlayers.add(getOfflinePlayer(name));
		}
		return bannedPlayers;
	}

	public GameMode getDefaultGameMode() {
		return defaultGameMode;
	}

	public void setDefaultGameMode(GameMode mode) {
		GameMode oldMode = defaultGameMode;
		defaultGameMode = mode;
		for (Player player : getOnlinePlayers()) {
			if (player.getGameMode() == oldMode) {
				player.setGameMode(mode);
			}
		}
		config.set("server.def-game-mode", mode.name());
	}

	public GameMode setDefaultGameMode(String mode) {
		GameMode gameMode;
		try {
			gameMode = GameMode.valueOf(mode);
		} catch (Throwable t) {
			logger.severe("Unknown game mode specified. Defaulting to survival");
			gameMode = GameMode.SURVIVAL;
		}
		setDefaultGameMode(gameMode);
		return getDefaultGameMode();
	}

	public int getViewDistance() {
		return config.getInt("server.view-distance", SpoutChunk.VISIBLE_RADIUS);
	}

	public String getLogFile() {
		return config.getString("server.log-file", "logs/log-%D.txt");
	}

	public SpoutMapView getMap(short id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public SpoutMapView createMap(World world) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getMotd() {
		return config.getString("server.motd", "Spout server");
	}

	public void setMotd(String motd) {
		config.set("server.motd", motd);
	}

	public StorageQueue getStorageQueue() {
		return storeQueue;
	}

	public boolean getFuzzyCommandMatching() {
		return config.getBoolean("server.fuzzy-command-matching", false);
	}

	/** The folder of world folders.
	 * @return The folder of world folders.
	 */
	public File getWorldContainer() {
		return new File(config.getString("server.folders.world-container", "."));
	}

	public void reloadConfiguration() {
		config.load();
	}
}
