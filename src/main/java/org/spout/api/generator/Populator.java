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
package org.spout.api.generator;

import java.util.Random;

import org.spout.api.geo.cuboid.Chunk;

public abstract class Populator {
	private boolean needsClearance;

	public Populator() {
		this(false);
	}

	public Populator(boolean needsClearance) {
		this.needsClearance = needsClearance;
	}

	public boolean needsClearance() {
		return needsClearance;
	}

	/**
	 * Populates the chunk.
	 *
	 * This method may make full use of the block modifying methods of the API.
	 *
	 * This method will be called once per chunk and it is guaranteed that a
	 * 2x2x2 cube of chunks containing the chunk will be loaded.
	 *
	 * The chunk to populate is the chunk with the lowest x, y and z coordinates
	 * of the cube.
	 *
	 * This allows the populator to create features that cross chunk boundaries.
	 *
	 * @param chunk the chunk to populate
	 * @param random The RNG for this chunk
	 */
	public abstract void populate(Chunk chunk, Random random);
}
