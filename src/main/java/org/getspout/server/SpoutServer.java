package org.getspout.server;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.getspout.api.GameMode;
import org.getspout.api.Player;
import org.getspout.api.Server;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandSource;
import org.getspout.api.entity.Entity;
import org.getspout.api.event.EventManager;
import org.getspout.api.geo.World;
import org.getspout.api.plugin.CommonPluginManager;
import org.getspout.api.plugin.Platform;
import org.getspout.api.plugin.Plugin;
import org.getspout.api.plugin.PluginManager;
import org.getspout.unchecked.api.inventory.Recipe;


public class SpoutServer implements Server {
	
	private volatile int version = 0;
	
	private volatile int maxPlayers = 20;
	
	private volatile String primaryAddress;
	
	private volatile String[] allAddresses;
	
	private File pluginDirectory = new File("plugins");
	
	private String name = "Spout Server";
	
	/**
	 * The set of allowable dimensions for the server
	 */
	private HashSet<String> dimensions = new HashSet<String>();
	
	/**
	 * This list of players for the server
	 */
	private LinkedHashSet<Entity> players = new LinkedHashSet<Entity>();
	private final static Entity[] emptyEntityArray = new Entity[0];
	
	/**
	 * The plugin manager for the server
	 */
	private PluginManager pluginManager = new CommonPluginManager(this, null, 0.0);
	
	/**
	 * The logger for this class.
	 */
	public static final Logger logger = Logger.getLogger("Minecraft");
	
	public static void main(String[] args) {
		org.getspout.unchecked.server.SpoutServer.main(args);
		
		//SpoutServer server = new SpoutServer();
		//server.start();
	}
	
	public void start() {
		
		// Start loading plugins
		loadPlugins();
	}
	
	public void loadPlugins() {
		
		pluginManager.clearPlugins();
		
		Plugin[] plugins = pluginManager.loadPlugins(pluginDirectory);
		for (Plugin plugin : plugins) {
			try {
				plugin.onLoad();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Error loading {0}: {1}", new Object[] {plugin.getDescription().getName(), ex.getMessage()});
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getVersion() {
		return version;
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
	public boolean hasDimension(String dimension) {
		return dimensions.contains(dimension);
	}

	@Override
	public void broadcastMessage(String message) {
		// TODO Auto-generated method stub
		
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
	public boolean processCommand(CommandSource sender, String commandLine) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File getUpdateFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Entity> matchPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld(UUID uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<World> getWorlds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(boolean worlds, boolean players) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean registerRecipe(Recipe recipe) {
		// TODO Auto-generated method stub
		return false;
	}

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameMode getDefaultGameMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultGameMode(GameMode mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public File getWorldFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command getRootCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventManager getEventManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Platform getPlatform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWhitelist() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWhitelist(boolean whitelist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWhitelist() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getWhitelistedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		// TODO Auto-generated method stub
		return false;
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
	public World loadWorld(String name, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player[] getOnlinePlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player getPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
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

}
