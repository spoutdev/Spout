/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.generator.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.util.cuboid.CuboidShortBuffer;

/**
 * Defines an abstract biome.
 */
public abstract class Biome {
	List<Decorator> decorators = new ArrayList<Decorator>();

	public Biome(Decorator... decorators) {
		this.decorators.addAll(Arrays.asList(decorators));
	}

	public final void decorate(Chunk chunk, Random random) {
		for (Decorator b : decorators) {
			b.populate(chunk, random);
		}
	}

	public abstract void generateColumn(CuboidShortBuffer blockData, int x, int chunkY, int z);

	public abstract String getName();
}
