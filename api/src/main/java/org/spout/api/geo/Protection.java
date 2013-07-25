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
package org.spout.api.geo;

import org.spout.api.entity.Entity;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;

/**
 * A Protection defines a volume within the game world that a plugin manages and protects. They are always limited to a specific world.
 */
public abstract class Protection implements WorldSource {
	private final World world;
	private final String name;
	private String exitMessage = null;
	private String enterMessage = null;

	public Protection(String name, World world) {
		this.name = name;
		this.world = world;
	}

	/**
	 * The name of the region
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The world that this region is in.
	 *
	 * @return the world
	 */
	@Override
	public World getWorld() {
		return world;
	}

	/**
	 * Checks if the given point is contained within the region.
	 *
	 * @return true, if the point is contained within this Region, otherwise false.
	 */
	public abstract boolean contains(Point point);

	/**
	 * Checks if the given block is contained within the region.
	 *
	 * @return true, if the point is contained within this Region, otherwise false.
	 */
	public boolean contains(Block block) {
		return contains(block.getPosition());
	}

	/**
	 * Checks if the given entity's position is contained within the region.
	 *
	 * @return true, if the entity is contained within this Region, otherwise false.
	 */
	public boolean contains(Entity entity) {
		return contains(entity.getPhysics().getPosition());
	}

	/**
	 * Generally, this is sent to a player when the player enters this region.
	 *
	 * @return the enter string.
	 */
	public String getEnterMessage() {
		return enterMessage;
	}

	/**
	 * Sets the enter message for this region.
	 */
	public void setEnterMessage(String message) {
		this.enterMessage = message;
	}

	/**
	 * Generally, this is sent to a player when the player exits this region.
	 *
	 * @return the exit string.
	 */
	public String getExitMessage() {
		return exitMessage;
	}

	/**
	 * Sets this region's exit message.
	 */
	public void setExitMessage(String message) {
		this.exitMessage = message;
	}
}
