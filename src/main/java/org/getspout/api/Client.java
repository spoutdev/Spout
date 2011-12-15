package org.getspout.api;

import java.io.File;

import org.getspout.api.entity.Player;
import org.getspout.api.plugin.PluginStore;
import org.getspout.api.util.Location;

/**
 * Represents the client-specific implementation of Minecraft.
 */
public interface Client extends Game{
	
	/**
	 * Gets the base addons folder.
	 * 
	 * @return addon folder
	 */
	public File getAddonFolder();
	
	/**
	 * Gets the location of the temporary audio cache. This cache is intended for ogg, wav, and midi type files.
	 * 
	 * This cache is purged when the game shuts down.
	 * 
	 * @return temporary audio cache.
	 */
	public File getAudioCache();
	
	/**
	 * Gets the location of the temporary general cache (used for non-texture and non-audio files).
	 * 
	 * This cache is purged when the game shuts down.
	 * 
	 * @return temporary cache.
	 */
	public File getTemporaryCache();
	
	/**
	 * Gets the location of the temporary image cache. This cache is intended for png type files.
	 * 
	 * @return temporary texture cache
	 */
	public File getTextureCache();
	
	/**
	 * Gets the location of the texture pack directory. This directory is used when users select a texture pack from the menu.
	 * 
	 * @return texture pack directory
	 */
	public File getTexturePackFolder();
	
	/**
	 * Gets the actively used texture pack zip, or null if no texture pack has been selected (default mc texture pack).
	 * 
	 * If the file exists, it will be a zip type file.
	 * 
	 * @return selected texture pack
	 */
	public File getSelectedTexturePackZip();
	
	/**
	 * Gets the location of the achievement and statistic folder for the local player.
	 * 
	 * @return statistic folder
	 */
	public File getStatsFolder();
	
	/**
	 * Gets the active player, connected to the local machine.
	 * 
	 * @return active player
	 */
	public Player getActivePlayer();
	
	/**
	 * Gets the active world, if a world is loaded. 
	 * 
	 * @return active world.
	 */
	public World getWorld();
	
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
	
	/**
	 * Gets client specific information for plugins.
	 * 
	 * @return plugin store
	 */
	public PluginStore getAddonStore();

}
