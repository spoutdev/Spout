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
package org.spout.api.entity.spawn;

import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.geo.discrete.Point;

/**
 * Represents an arrangement for spawning entities
 */
public interface SpawnArrangement {
	/**
	 * Gets the controller types to spawn. If this array is of length one,
	 * then that controller should be used for all entities.
	 * @return the controller
	 */
	public ControllerType[] getControllerTypes();

	/**
	 * Gets the Points to spawn entities at. The getController method
	 * is called to determine which entity for a given index.
	 * @return the spawn points in an array
	 */
	public Point[] getArrangement();
}
