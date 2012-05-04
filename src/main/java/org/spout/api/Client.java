/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api;

import java.io.File;
import java.util.Stack;

import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.gui.Screen;
import org.spout.api.plugin.PluginStore;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;

/**
 * Represents the client-specific implementation of Minecraft.
 */
public interface Client extends Engine {
	
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
	 * Gets the location of the texture pack directory. This directory is used
	 * when users select a texture pack from the menu.
	 *
	 * @return texture pack directory
	 */
	public File getResourcePackFolder();

	
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
	 * The Camera object is the viewport into the scene
	 * 
	 *
	 * @return the location and view of the camera
	 */
	public Camera getActiveCamera();

	/**
	 * Sets the camera to the active camera.
	 * There can only be one camera active at a time
	 */
	public void setActiveCamera(Camera activeCamera);


	/**
	 * Gets client specific information for plugins.
	 *
	 * @return plugin store
	 */
	public PluginStore getPluginStore();
	
	/**
	 * Gets the current render mode that spoutclient is running in
	 * 
	 * @return render mode
	 */
	public RenderMode getRenderMode();
	
	/**
	 * @return the stack of the screens.
	 */
	public Stack<Screen> getScreenStack();

	/**
	 * Puts the given screen on the stack.
	 * @param screen the screen to put on the stack
	 */
	public void openScreen(Screen screen);

	/**
	 * Removes the screen in focus from the stack.
	 */
	public void closeScreen();

	/**
	 * Removes the given screen from the stack. Useful if the screen you want to close is not in focus.
	 * @param screen The screen to close
	 */
	public void closeScreen(Screen screen);

	/**
	 * Gets the screen in focus. This is the screen that is in the front of the stack.
	 * @return the screen in focus.
	 */
	public Screen getFocusedScreen();
}
