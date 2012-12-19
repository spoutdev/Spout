/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.exception;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.plugin.Plugin;

/**
 * Exception which is thrown when the maximum length of the cause chain is reached.
 * This is a save guard against memory leaks due to redstone clocks and similar.
 */
public class MaxCauseChainReached extends RuntimeException {
	public MaxCauseChainReached(Point point) {
		super("at location: " + point.toBlockString());
	}

	public MaxCauseChainReached(Plugin plugin) {
		super("by a cause from plugin " + plugin.getName());
	}

	public MaxCauseChainReached(World world) {
		super("by a cause from world " + world.getName());
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}

