package org.getspout.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.getspout.api.ChatColor;
import org.getspout.api.Server;
import org.getspout.api.Spout;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandException;
import org.getspout.api.command.CommandRegistrationsFactory;
import org.getspout.api.command.CommandSource;
import org.getspout.api.command.RootCommand;
import org.getspout.api.command.WrappedCommandException;
import org.getspout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.getspout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.getspout.api.command.annotated.SimpleInjector;
import org.getspout.api.event.EventManager;
import org.getspout.api.event.Order;
import org.getspout.api.event.SimpleEventManager;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.api.event.server.PreCommandEvent;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.player.Player;
import org.getspout.api.plugin.CommonPluginLoader;
import org.getspout.api.plugin.CommonPluginManager;
import org.getspout.api.plugin.Platform;
import org.getspout.api.plugin.Plugin;
import org.getspout.api.plugin.PluginManager;
import org.getspout.api.plugin.security.CommonSecurityManager;
import org.getspout.api.protocol.CommonPipelineFactory;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.SessionRegistry;
import org.getspout.api.util.config.Configuration;
import org.getspout.server.command.AdministrationCommands;
import org.getspout.server.datatable.SpoutDatatableMap;
import org.getspout.server.datatable.value.SpoutDatatableInt;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.io.StorageQueue;
import org.getspout.server.net.SpoutSession;
import org.getspout.server.net.SpoutSessionRegistry;
import org.getspout.server.player.SpoutPlayer;
import org.getspout.server.scheduler.SpoutScheduler;
import org.getspout.server.util.thread.AsyncManager;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableConcurrentHashMap;
import org.getspout.server.util.thread.snapshotable.SnapshotableConcurrentLinkedHashMap;
import org.getspout.server.util.thread.snapshotable.SnapshotableReference;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class SpoutServer extends AsyncManager implements Server {

	private volatile int maxPlayers = 20;

	private volatile String primaryAddress;

	private volatile String[] allAddresses;

	private File pluginDirectory = new File("plugins");

	private File configDirectory = new File("config");

	private File updateDirectory = new File("update");

	private String logFile = "logs/log-%D.txt";

	private String name = "Spout Server";

	private EntityManager entityManager = new EntityManager();

	private SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * Default world generator
	 */
	private WorldGenerator defaultGenerator = null;

	/**
	 * Online player list
	 */
	private final SnapshotableConcurrentLinkedHashMap<String, Player> players = new SnapshotableConcurrentLinkedHashMap<String, Player>(snapshotManager, null);

	/**
	 * The security manager
	 * TODO - need to integrate this
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
	 * The scheduler for the server
	 */
	private final SpoutScheduler scheduler = new SpoutScheduler(this);

	/**
	 * If the server has a whitelist or not
	 */
	private volatile boolean whitelist = false;

	/**
	 * If the server allows flight
	 */
	private volatile boolean allowFlight = false;

	/**
	 * A list of all players who can log onto this server, if using a whitelist.
	 */
	private List<String> whitelistedPlayers = new ArrayList<String>();

	/**
	 * loaded plugins
	 */
	private SnapshotableConcurrentLinkedHashMap<String, SpoutWorld> loadedWorlds = new SnapshotableConcurrentLinkedHashMap<String, SpoutWorld>(snapshotManager, null);

	private SnapshotableReference<World> defaultWorld = new SnapshotableReference<World>(snapshotManager, null);

	/**
	 * The root commnd for this server
	 */
	private final RootCommand rootCommand = new RootCommand(this);

	/**
	 * The event manager
	 */
	private final EventManager eventManager = new SimpleEventManager();

	public SpoutServer() {
		super(1, new ThreadAsyncExecutor());
		registerWithScheduler(scheduler);
		init();
		if (!getExecutor().startExecutor()) {
			throw new IllegalStateException("SpoutServer's executor was already started");
		}
	}

	private void testDatatable() {
		long time = System.currentTimeMillis();
		SpoutDatatableMap table = new SpoutDatatableMap(SpoutEntity.entityStringMap);
		for (int i = 0; i < 1000; i++) {
			table.set(new SpoutDatatableInt(SpoutEntity.entityStringMap.register("" + i), i));

		}
		long entry = System.currentTimeMillis() - time;
		System.out.println("Datatable Entry Time: " + entry / 1000.f);

		File out = new File("datatable.dat");
		if (!out.exists())
			try {
				out.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream(10000);

			long outputspeed = System.currentTimeMillis();
			table.output(stream);
			long asdf = System.currentTimeMillis() - outputspeed;
			System.out.println("Datatable serialization Time: " + asdf / 1000.f);
			FileOutputStream s = new FileOutputStream(out);
			s.write(stream.toByteArray());
			s.flush();
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		SpoutServer server = new SpoutServer();
		server.start();

		server.bind(new InetSocketAddress("localhost", 25565));

	}

	public void start() {
		Spout.setGame(this);

		CommandRegistrationsFactory<Class<?>> commandRegFactory =
				new AnnotatedCommandRegistrationFactory(new SimpleInjector(this),
						new SimpleAnnotatedCommandExecutorFactory());

		// Register commands
		getRootCommand().addSubCommands(this, AdministrationCommands.class, commandRegFactory);

		consoleManager.setupConsole();

		try {
			loadConfig();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse config", t);
		}

		// Start loading plugins
		loadPlugins();
		enablePlugins();

		getEventManager().registerEvents(new InternalEventListener(this), this);
		scheduler.startMainThread();
	}

	@SuppressWarnings("unchecked")
	private void loadConfig() throws IOException {
		Configuration config = getConfiguration();
		boolean save = false;
		try {
			Map<String, String> generators = (Map<String, String>) config.getProperty("worlds");
			if (generators == null) {
				generators = new HashMap<String, String>();
				generators.put("world", "default");
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
			this.whitelistedPlayers = whitelist;
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
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read banlist from the config file!");
		}
		try {
			Boolean fly = (Boolean) config.getProperty("allowflight");
			if (fly == null) {
				fly = false;
				config.setProperty("allowflight", false);
				save = true;
			}
			allowFlight = fly;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read flight permissions from the config file!");
		}
		try {
			Boolean whitelist = (Boolean) config.getProperty("usewhitelist");
			if (whitelist == null) {
				whitelist = false;
				config.setProperty("usewhitelist", false);
				save = true;
			}
			this.whitelist = whitelist;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read whitelist value from the config file!");
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

		if (!pluginDirectory.exists())
			pluginDirectory.mkdirs();

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
	public void bind(SocketAddress address) {
		logger.log(Level.INFO, "Binding to address: {0}...", address);
		group.add(bootstrap.bind(address));
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
			if (event.isCancelled()) return;
			commandLine = event.getMessage();
			getRootCommand().execute(source, commandLine.split(" "), -1, false);
		} catch (WrappedCommandException e) {
			source.sendMessage(ChatColor.RED + "Internal error executing command!");
			source.sendMessage(ChatColor.RED + "Error: " + e.getMessage() + "; See console for details.");
			e.printStackTrace();
		} catch (CommandException e) {
			// TODO: Better exception handling!
			source.sendMessage(ChatColor.RED + e.getMessage());
		}
	}

	@Override
	public File getUpdateFolder() {
		if (!updateDirectory.exists()) updateDirectory.mkdirs();
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
		return loadedWorlds.getValue(name);
	}

	@Override
	public World getWorld(UUID uid) {
		for (SpoutWorld world : loadedWorlds.getValues()) {
			if (world.getUID().equals(uid)) return world;
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
	public int getSpawnProtectRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSpawnProtectRadius(int radius) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean allowFlight() {
		return allowFlight;
	}

	@Override
	public File getWorldFolder() {
		// TODO Auto-generated method stub
		return null;
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
			Configuration config = getConfiguration();
			List<String> whitelist = (List<String>) config.getProperty("whitelist");
			if (whitelist != null) {
				this.whitelistedPlayers = whitelist;
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

	@Override
	public boolean unloadWorld(String name, boolean save) {
		return unloadWorld(loadedWorlds.getValue(name), save);
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
		SpoutWorld world = new SpoutWorld(name, this, 0);

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
	public boolean isOnlineMode() {
		// TODO Auto-generated method stub
		return false;
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
	public void unban(String address) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	private Player[] emptyPlayerArray = new Player[0];
	@Override
	public Player[] getOnlinePlayers() {
		return players.get().values().toArray(emptyPlayerArray);
	}

	@Override
	public Player getPlayer(String name, boolean exact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Player> getBannedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Player> getOps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session newSession(Channel channel) {
		return new SpoutSession(this, channel);
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
		group.close();
		bootstrap.getFactory().releaseExternalResources();

		// And finally kill the console
		consoleManager.stop();
		scheduler.stop();
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();
		snapshotManager.copyAllSnapshots();
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
		if (!configDirectory.exists()) configDirectory.mkdirs();
		return configDirectory;
	}

	@Override
	public WorldGenerator getDefaultGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultGenerator(WorldGenerator generator) {
		// TODO Auto-generated method stub

	}

	private Configuration getConfiguration() throws IOException {
		File configFile = new File(getConfigFolder(), "spout.yml");
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		Configuration config = new Configuration(configFile);
		config.load();
		return config;
	}

	public EntityManager getExpectedEntityManager(Point point) {
		Region region = point.getWorld().getRegion(point);
		return ((SpoutRegion) region).getEntityManager();
	}

	public EntityManager getExpectedEntityManager(World world) {
		return ((SpoutWorld) world).getEntityManager();
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshot();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	// Players should use weak map?
	public Player addPlayer(String playerName, SpoutSession session) {
		Player player = null;

		boolean success = false;
		
		while (!success) {
			player = players.getLive().get(playerName);

			if (player != null) {
				if (!((SpoutPlayer)player).connect(session, new SpoutEntity(this))) {
					// Means player was already online
					return null;
				} else {
					success = true;
				}
			} else {
				player = new SpoutPlayer(playerName, new SpoutEntity(this), session);
				if (players.putIfAbsent(playerName, player) == null) {
					success = true;
				}
			}
		}
		if (player == null) {
			throw new IllegalStateException("Attempting to set session to null player, which shouldn't be possible");
		} else {
			session.setPlayer(player);
		}
		return player;
	}

}
