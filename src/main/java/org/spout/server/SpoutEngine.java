package org.spout.server;

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
import org.spout.api.Game;
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
import org.spout.api.event.world.WorldLoadEvent;
import org.spout.api.event.world.WorldUnloadEvent;
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
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.util.config.Configuration;
import org.spout.server.command.AdministrationCommands;
import org.spout.server.command.MessagingCommands;
import org.spout.server.entity.EntityManager;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.net.SpoutSession;
import org.spout.server.net.SpoutSessionRegistry;
import org.spout.server.player.SpoutPlayer;
import org.spout.server.scheduler.SpoutScheduler;
import org.spout.server.util.config.SpoutConfiguration;
import org.spout.server.util.thread.AsyncManager;
import org.spout.server.util.thread.ThreadAsyncExecutor;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableLinkedHashMap;
import org.spout.server.util.thread.snapshotable.SnapshotableReference;

public class SpoutEngine extends AsyncManager implements Game {
	private volatile int maxPlayers = 20;

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
	protected final ChannelGroup group = new DefaultChannelGroup();

	/**
	 * The network executor service - Netty dispatches events to this thread
	 * pool.
	 */
	protected final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * A list of all the active {@link SpoutSession}s.
	 */
	protected final SpoutSessionRegistry sessions = new SpoutSessionRegistry();

	/**
	 * The scheduler for the server.
	 */
	private final SpoutScheduler scheduler = new SpoutScheduler(this);
	
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

	protected final ConcurrentMap<SocketAddress, BootstrapProtocol> bootstrapProtocols = new ConcurrentHashMap<SocketAddress, BootstrapProtocol>();

	private final Random random = new Random();

	private boolean debugMode = false;

	
	/**
	 * Cached copy of the server configuration, can be used instead of
	 * re-parsing the config file for each access
	 */
	protected SpoutConfiguration config = new SpoutConfiguration();

	
	public SpoutEngine(String[] args){
		super(1, new ThreadAsyncExecutor());
		for(String s : args){
			if(s.equals("-debug")) debugMode = true;
		}
		registerWithScheduler(scheduler);
		if (!getExecutor().startExecutor()) {
			throw new IllegalStateException("SpoutServer's executor was already started");
		}
	}
	
	public void init(){
		
	}
	
	public void start(){
		Spout.setGame(this);

		if(debugMode()){
			getLogger().warning("Spout has been started in Debug Mode!  This mode is for developers only");

		}

		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());

		// Register commands
		getRootCommand().addSubCommands(this, AdministrationCommands.class, commandRegFactory);
		getRootCommand().addSubCommands(this, MessagingCommands.class, commandRegFactory);
		if(Spout.getGame().debugMode()) getRootCommand().addSubCommands(this, TestCommands.class, commandRegFactory);

		consoleManager.setupConsole();

		config.load();
		
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

	

	public Configuration getConfiguration() {
		return config;
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


	private Player[] emptyPlayerArray = new Player[0];


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
		if(loadedWorlds.get().containsKey((name))) return loadedWorlds.get().get(name);
		if(loadedWorlds.getLive().containsKey(name)) return loadedWorlds.getLive().get(name);

		// TODO - should include generator (and non-zero seed)
		if (generator == null) {
			generator = defaultGenerator;
		}


		SpoutWorld world = new SpoutWorld(name, this, random.nextLong(), generator);


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
		for (Player player : players.get().values()) {
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
					//TODO Save the world, save the cheerleader
				}
				//Note: Worlds should not allow being saved twice and/or throw exceptions if accessed after unloading
				//      Also, should blank out as much internal world data as possible, in case plugins retain references to unloaded worlds
			}
			return success;
		}
	}

	public EntityManager getExpectedEntityManager(Point point) {
		Region region = point.getWorld().getRegion(point);
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


}
