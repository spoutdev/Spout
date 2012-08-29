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
package org.spout.api.component;

import com.alta189.annotations.RequireDefault;
import org.spout.api.component.components.DatatableComponent;
import org.spout.api.tickable.Tickable;

@RequireDefault
public interface Component extends Tickable {
	/**
	 * Attaches to a component holder.
	 * @param holder The component holder to attach to
	 */
	public void attachTo(ComponentHolder<?> holder);
	
	/**
	 * Gets the component holder holding this component.
	 * @return the component holder
	 */
	public ComponentHolder<?> getHolder();

	/**
	 * Called when this component is attached to a holder
	 */
	public void onAttached();

	/**
	 * Called when this component is detached from a holder.
	 */
	public void onDetached();

	/**
	 * Called when the parent entity leaves the world.
	 */
	public void onRemoved();

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
}
