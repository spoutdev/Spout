/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api;

import java.io.File;

import org.getspout.api.entity.Entity;
import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.plugin.PluginStore;

/**
 * Represents the client-specific implementation of Minecraft.
 */
public interface Client extends Game {

	/**
	 * Gets the base addons folder.
	 *
	 * @return addon folder
	 */
	public File getAddonFolder();

	/**
	 * Gets the location of the temporary audio cache. This cache is intended
	 * for ogg, wav, and midi type files.
	 *
	 * This cache is purged when the game shuts down.
	 *
	 * @return temporary audio cache.
	 */
	public File getAudioCache();

	/**
	 * Gets the location of the temporary general cache (used for non-texture
	 * and non-audio files).
	 *
	 * This cache is purged when the game shuts down.
	 *
	 * @return temporary cache.
	 */
	public File getTemporaryCache();

	/**
	 * Gets the location of the temporary image cache. This cache is intended
	 * for png type files.
	 *
	 * @return temporary texture cache
	 */
	public File getTextureCache();

	/**
	 * Gets the location of the texture pack directory. This directory is used
	 * when users select a texture pack from the menu.
	 *
	 * @return texture pack directory
	 */
	public File getTexturePackFolder();

	/**
	 * Gets the actively used texture pack zip, or null if no texture pack has
	 * been selected (default mc texture pack).
	 *
	 * If the file exists, it will be a zip type file.
	 *
	 * @return selected texture pack
	 */
	public File getSelectedTexturePackZip();

	/**
	 * Gets the location of the achievement and statistic folder for the local
	 * player.
	 *
	 * @return statistic folder
	 */
	public File getStatsFolder();

	/**
	 * Gets the active player, connected to the local machine.
	 *
	 * @return active player
	 */
	public Entity getActivePlayer();

	/**
	 * Gets the active world, if a world is loaded.
	 *
	 * @return active world.
	 */
	public World getWorld();

	/**
	 * The camera property holds the position and view of the camera. You can
	 * set it to a new location to influence it and to provide camera cutscenes.
	 *
	 * @return the location and view of the camera
	 */
	public Point getCamera();

	/**
	 * The camera property holds the position and view of the camera. You can
	 * set it to a new location to influence it and to provide camera cutscenes.
	 * Detaching the camera is mandatory before doing a cut scene, otherwise,
	 * the players movement will override your cutscene.
	 *
	 * @see detachCamera(boolean)
	 * @param loc the location and view of the camera
	 */
	public void setCamera(Point loc);

	/**
	 * The detach property decides if player movements will influence the camera
	 * or not. If the camera is detached, player movements will be ignored.
	 *
	 * @param detach if the camera should be detached
	 */
	public void detachCamera(boolean detach);

	/**
	 * The detach property decides if player movements will influence the camera
	 * or not. If the camera is detached, player movements will be ignored.
	 *
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
