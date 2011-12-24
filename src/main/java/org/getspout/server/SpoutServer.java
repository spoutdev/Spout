package org.getspout.server;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.getspout.api.GameMode;
import org.getspout.api.Server;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandSource;
import org.getspout.api.entity.Entity;
import org.getspout.api.event.EventManager;
import org.getspout.api.geo.World;
import org.getspout.api.plugin.Platform;
import org.getspout.api.plugin.PluginManager;
import org.getspout.unchecked.api.OfflinePlayer;
import org.getspout.unchecked.api.inventory.Recipe;


public class SpoutServer implements Server {
	public static void main(String[] args) {
		org.getspout.unchecked.server.SpoutServer.main(args);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Entity[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxPlayers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNether() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTheEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void broadcastMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginManager getPluginManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
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
	public Entity getPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getPlayer(String name, boolean exact) {
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
	public OfflinePlayer getOfflinePlayer(String name) {
		// TODO Auto-generated method stub
		return null;
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
	public Collection<OfflinePlayer> getBannedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<OfflinePlayer> getOps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World loadWorld(String name, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

}
