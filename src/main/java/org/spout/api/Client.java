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

import org.spout.api.audio.SoundManager;
import org.spout.api.entity.Player;
import org.spout.api.geo.World;
import org.spout.api.gui.ScreenStack;
import org.spout.api.input.InputManager;
import org.spout.api.math.Vector2;
import org.spout.api.protocol.PortBinding;
import org.spout.api.render.RenderMode;

/**
 * Represents the client-specific component of the Spout platform.
 */
public interface Client extends Engine {
	/**
	 * Gets the player on the local machine (the one who is using the client).
	 * @return player
	 */
	public Player getPlayer();

	/**
	 * Gets the current world in-which the player on the local machine is within.
	 *
	 * This is always the world the client is currently rendering.
	 * @return world
	 */
	public World getWorld();

	/**
	 * Gets the current {@link RenderMode} that the client is running in.
	 * @return render mode
	 */
	public RenderMode getRenderMode();

	/**
	 * Gets the sound manager for the client. Used to create sound sources.
	 * @return The client's sound manager.
	 */
	public SoundManager getSoundManager();

	/**
	 * Gets the input manager for the client. Keybindings are registered here.
	 * @return The client's input manager
	 */
	public InputManager getInputManager();

	/**
	 * Returns the current IP address the client is connected to.
	 * If the client is not connected to a server, this returns null.
	 * @return address
	 */
	public PortBinding getAddress();

	/**
	 * Returns the resolution of the window, in pixels.
	 * @return the resolution of the window.
	 */
	public Vector2 getResolution();

	/**
	 * Returns the aspect ratio of the client, in pixels.
	 * <p>
	 * Ratio = (screen width / screen height)
	 * @return The ratio as a float
	 */
	public float getAspectRatio();

	/**
	 * Returns the screen stack.
	 * @return the screen stack.
	 */
	public ScreenStack getScreenStack();
}
