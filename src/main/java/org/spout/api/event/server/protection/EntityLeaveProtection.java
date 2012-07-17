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
package org.spout.api.event.server.protection;

import org.spout.api.entity.Entity;
import org.spout.api.event.HandlerList;
import org.spout.api.event.entity.EntityEvent;
import org.spout.api.geo.Protection;

/**
 * An event that should be fired by a plugin when a player leaves a protection.  
 */
public class EntityLeaveProtection  extends EntityEvent {

	private static HandlerList handlers = new HandlerList();
	
	private final Protection protection;
	
	private String message;
	
	public EntityLeaveProtection(Entity entity, Protection protection, String message) {
		super(entity);
		this.protection = protection;
		this.setMessage(message);
	}

	/**
	 * The message that will be sent to the entity once they leave the protection, or null if none.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to send to the entity when they leave the protection.
	 * 
	 * @param the message to send
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the protection the player is leaving
	 * 
	 * @return the protection
	 */
	public Protection getProtection() {
		return protection;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public HandlerList getHandlerList() {
		return handlers;
	}
}
