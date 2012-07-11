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
package org.spout.api.event.player;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.player.Player;

/**
 * Called when input from the player is detected.
 * Implements {@link Cancellable}. If cancelled, the system will ignore the key press or release occurring.
 *
 */
public class PlayerKeyEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Keyboard key;

	private boolean pressed;

	public PlayerKeyEvent(Player p) {
		super(p);
	}

	/**
	 * The key involved in the event.
	 * 
	 * @return key that is involved.
	 */
	public Keyboard getKey() {
		return key;
	}

	/**
	 * Set the key that is detected.
	 * 
	 * @param key
	 */
	public void setKey(Keyboard key) {
		this.key = key;
	}

	/**
	 * Checks if the key is currently being pressed during this event.
	 * 
	 * @return true, if pressed, otherwise false.
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * Checks if the key is currently released.
	 * 
	 * @return true, if released, otherwise false.
	 */
	public boolean isReleased() {
		return !pressed;
	}

	/**
	 * Sets whether the key is being pressed, or released.
	 * 
	 * @param pressed
	 */
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
