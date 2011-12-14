package org.getspout.commons;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.getspout.commons.command.AddonCommand;
import org.getspout.commons.command.CommandSender;
import org.getspout.commons.World;
import org.getspout.commons.entity.ActivePlayer;
import org.getspout.commons.entity.Player;
import org.getspout.commons.plugin.PluginManager;
import org.getspout.commons.plugin.PluginStore;
import org.getspout.commons.util.Location;

/**
 * Represents the instance of the game, whether it be a server instance or client instance
 * (Takes the place of the previous Bukkit.getServer or Spoutcraft.getClient())
 */
public interface Game {

	public String getName();

	public long getVersion();
	
	public World getWorld();

	public PluginManager getAddonManager();

	public Logger getLogger();

	public AddonCommand getAddonCommand(String name);

	public boolean dispatchCommand(CommandSender sender, String commandLine);

	public File getUpdateFolder();

	public boolean isSpoutEnabled();

	public long getServerVersion();

	public File getAddonFolder();
	
	public File getAudioCache();
	
	public File getTemporaryCache();
	
	public File getTextureCache();
	
	public File getTexturePackFolder();
	
	public File getSelectedTexturePackZip();
	
	public File getStatsFolder();

	public long getTick();

	public Mode getMode();
	
	public ActivePlayer getActivePlayer();
	
	public PluginStore getAddonStore();
	
	/**
	 * Gets a list of all Players
	 *
	 * @return An array of Players
	 */
	public Player[] getPlayers();
	
	/**
	 * Gets a player object by the given username
	 *
	 * This method may not return objects for offline players
	 *
	 * @param name Name to look up
	 * @return Player if it was found, otherwise null
	 */
	public Player getPlayer(String name);

	/**
	 * Gets the player with the exact given name, case insensitive
	 *
	 * @param name Exact name of the player to retrieve
	 * @return Player object or null if not found
	 */
	public Player getPlayerExact(String name);

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
	public List<Player> matchPlayer(String name);

	/**
	 * The camera property holds the position and view of the camera. You can set it to a new location to influence it and to provide camera cutscenes.
	 * @return the location and view of the camera
	 */
	public Location getCamera();

	/**
	 * The camera property holds the position and view of the camera. You can set it to a new location to influence it and to provide camera cutscenes.
	 * Detaching the camera is mandatory before doing a cut scene, otherwise, the players movement will override your cutscene.
	 * @see detachCamera(boolean)
	 * @param loc the location and view of the camera
	 */
	public void setCamera(Location loc);
	
	/**
	 * The detach property decides if player movements will influence the camera or not. If the camera is detached, player movements will be ignored.
	 * @param detach if the camera should be detached
	 */
	public void detachCamera(boolean detach);
	
	/**
	 * The detach property decides if player movements will influence the camera or not. If the camera is detached, player movements will be ignored.
	 * @return if the camera is detached
	 */
	public boolean isCameraDetached();

	public enum Mode {
		Single_Player,
		Multiplayer,
		Menu;
	}
}
