/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.gamestate;

import org.spout.api.tickable.BasicTickable;

/**
 * Simple Game State interface
 */
public abstract class GameState extends BasicTickable {
	/**
	 * Called when this state is going to be used.  Use this to initialize state variables. 
	 */
	public abstract void initialize();
	
	/**
	 * Called when this state can load resources. 
	 */
	public void loadResources() { }
	
	/**
	 * Called ever render tick on the client
	 * @param dt - time since last render in seconds
	 */
	public abstract void onRender(float dt);
	/**
	 * Called when the state is being torn down.  If there are any native resources loaded, unload them here
	 */
	public void unloadResources() { }
	
	/**
	 * Called when the player pauses this state.
	 */
	public void onPause() { }
	
	/**
	 * Called when the player unpauses this state	
	 */
	public void onUnPause() { }
	
}
