/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api;

import java.util.UUID;

import org.spout.api.audio.SoundManager;
import org.spout.api.geo.World;
import org.spout.api.gui.ScreenStack;
import org.spout.api.input.InputManager;
import org.spout.api.math.Vector2;
import org.spout.api.entity.Player;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.PortBinding;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;

/**
 * Represents the client-specific component of the Spout platform.
 */
public interface Client extends Engine {
	/**
	 * Gets the active player, connected to the local machine.
	 *
	 * @return active player
	 */
	public Player getActivePlayer();

	/**
	 * The Camera object is the viewport into the scene
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
	 * Gets the current {@link RenderMode} that the client is running in.
	 *
	 * @return render mode
	 */
	public RenderMode getRenderMode();

	/**
	 * Gets the sound manager for the client. Used to create sound sources.
	 * 
	 * @return The client's sound manager.
	 */
	public SoundManager getSoundManager();

	/**
	 * Gets the input manager for the client. Keybindings are registered here.
	 *
	 * @return The client's input manager
	 */
	public InputManager getInputManager();

	/**
	 * Returns the current IP address the client is connected to.
	 * If the client is not connected to a server, this returns null.
	 *
	 * @return address
	 */
	public PortBinding getAddress();

	/**
	 * This method is called to notify the client that its world needs to change
	 *
	 * @param name The name of the new world
	 * @param uuid The world's uuid
	 * @param datatable The world's datatable data
	 * @return The new world
	 */
	public World worldChanged(String name, UUID uuid, byte[] datatable);

	/**
	 * Returns the resolution of the window, in pixels.
	 * 
	 * @return the resolution of the window.
	 */
	public Vector2 getResolution();

	/**
	 * Returns the aspect ratio of the client, in pixels.
	 *
	 * Ratio = (screen width / screen height)
	 * @return The ratio as a float
	 */
	public float getAspectRatio();

	/**
	 * Returns the screen stack.
	 * 
	 * @return the screen stack.
	 */
	public ScreenStack getScreenStack();
}
