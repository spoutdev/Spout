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
package org.spout.api.entity;

import org.spout.api.entity.components.DatatableComponent;
import org.spout.api.entity.components.TransformComponent;
import org.spout.api.tickable.Tickable;

/**
 * Represents an attachment to a entity that can respond to Ticks.
 */
public interface Component extends Tickable {
	/**
	 * Attaches this component to a entity
	 * @param parent entity this component will be attached to.
	 */
	public void attachToEntity(Entity parent);

	/**
	 * Gets the parent entity associated with this component.
	 * @return the parent entity
	 */
	public Entity getParent();

	/**
	 * Called when this component is attached to a entity.
	 */
	public abstract void onAttached();

	/**
	 * Called when this component is detached from a entity..
	 */
	public void onDetached();
	
	/**
	 * Called when the parent entity is spawned into the world.
	 */
	public void onSpawned();
	
	/**
	 * Called when the parent entity leaves the world.
	 */
	public void onRemoved();

	/**
	 * Called when the entity changes from unobserved to observed.
	 */
	public void onObserved();
	
	/**
	 * Called when the entity changes from observed to unobserved.
	 */
	public void onUnObserved();
	
	/**
	 * Called when the entity is set to be sync'd to clients.
	 * 
	 * Updates are NOT ALLOWED within this method.
	 */
	public void onSync();
	
	/**
	 * Returns the datatable component attached to the parent entity. This component always exists.
	 * @return The datatable component
	 */
	public DatatableComponent getDatatable();
	
	/**
	 * Returns the transform component attached to the parent entity. This component always exists.
	 * @return The transform component
	 */
	public TransformComponent getTransform();
}
