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
package org.spout.api.event.entity;

import org.spout.api.Source;
import org.spout.api.entity.component.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.event.HandlerList;

/**
 * Called when an entity's {@link Controller} is changing.
 */
public class EntityControllerChangeEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();

	private final Source source;

	private Controller newController;

	public EntityControllerChangeEvent(Entity e, Source source, Controller newController) {
		super(e);
		this.source = source;
		this.newController = newController;
	}

	/**
	 * Gets the source of this event.
	 *
	 * @return the source of the event.
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Gets the new controller of the entity.
	 *
	 * @return The new controller.
	 */
	public Controller getNewController() {
		return newController;
	}

	/**
	 * Sets the controller of the entity.
	 *
	 * @param newController The new controller of the entity.
	 */
	public void setNewController(Controller newController) {
		this.newController = newController;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
