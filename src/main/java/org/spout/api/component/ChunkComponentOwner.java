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
import java.util.Collections;

import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.component.components.DatatableComponent;
import org.spout.api.geo.cuboid.Chunk;

public class ChunkComponentOwner implements ComponentOwner {
	private final DatatableComponent data = new DatatableComponent();
	private final Chunk chunk;
	private final int x, y, z;
	public ChunkComponentOwner(Chunk chunk, int x, int y, int z) {
		this.chunk = chunk;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	@Override
	public Collection<Component> values() {
		return Collections.emptyList();
	}

	@Override
	public DatatableComponent getData() {
		return data;
	}

	/**
	 * Gets the chunk this component is in
	 * 
	 * @return chunk
	 */
	public Chunk getChunk() {
		return chunk;
	}

	/**
	 * Gets the world block x-coordinate
	 * 
	 * @return world x-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the world block y-coordinate
	 * 
	 * @return world y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the world block z-coordinate
	 * 
	 * @return world z-coordinate
	 */
	public int getZ() {
		return z;
	}
}