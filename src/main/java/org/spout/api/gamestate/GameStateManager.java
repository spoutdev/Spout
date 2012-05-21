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

import java.util.Stack;

import org.spout.api.Tickable;

public class GameStateManager implements Tickable {
	private Stack<GameState> states = new Stack<GameState>();
	
	public void pushState(GameState state) {
		if (states.peek() != null) states.peek().onPause(); //Pause the current state
		
		states.push(state); //Push the state onto the top of the stack
		
		state.initialize();
		
		state.loadResources();
		
	}
	
	public GameState popState() {
		GameState head = states.pop(); //remove the current state from the stack
		head.unloadResources();
		if (states.peek() != null) states.peek().onUnPause(); //unpause the previous state
		return head;
	}

	@Override
	public void onTick(float dt) {
		if (states.peek() != null) states.peek().onTick(dt); //tick the current state
	}
	
	public void onRender(float dt) {
		if (states.peek() != null) states.peek().onTick(dt);
	}
	
	
}
