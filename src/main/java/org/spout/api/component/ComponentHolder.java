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

import java.util.Collection;

import org.spout.api.component.components.BlockComponent;
import org.spout.api.component.components.DatatableComponent;

/**
 * Represents the accessing portion of an entity that controls retrieving/removing components.
 */
public interface ComponentHolder {

	public <T extends Component> T addComponent(T component);

	public <T extends Component> T addComponent(Class<? extends Component> type, T component);

	public <T extends Component> T removeComponent(Class<? extends Component> type);

	public <T extends Component> T getComponent(Class<T> type);

	public <T extends Component> T getOrCreate(Class<? extends Component> typeClass);

	public boolean hasComponent(Class<? extends Component> class1);

	/**
	 * Gets all components held by this component holder.
	 * @return The components held by this holder
	 */
	public Collection<Component> getComponents();
	
	/**
	 * Gets the datatable component held by this component holder.
	 * @return Gets the datatable held by this holder
	 */
	public DatatableComponent getDatatable();
}
