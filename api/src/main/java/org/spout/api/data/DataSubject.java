/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.data;

import org.spout.api.geo.World;
import org.spout.api.util.Named;

public interface DataSubject extends Named {
	/**
	 * Gets data from the given node
	 *
	 * @return null if the data is not set
	 */
	public ValueHolder getData(String node);

	/**
	 * Gets data from the given node and world
	 *
	 * @return null if the data is not set
	 */
	public ValueHolder getData(World world, String node);

	/**
	 * Whether or not the subject has the data in it's current world.
	 *
	 * @return true if has data
	 */
	public boolean hasData(String node);

	/**
	 * Whether or not the subject has the data in the specified world.
	 *
	 * @return true if has data
	 */
	public boolean hasData(World world, String node);
}
