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
package org.spout.api.generator.biome.selector;

import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeSelector;

/**
 * A layered biome selector. This selector starts with a starting layer which
 * will pick an element for the same seed and coordinates. If the element is a
 * biome, it will be returned, else if it is a layer, the selector will use this
 * layer to pick a new element. The selector will treat this layer like the
 * first one, and will continue the cycle until it finds a biome or a null
 * element. If a null element is found, the fallback biome will be returned.
 */
public class LayeredBiomeSelector extends BiomeSelector {
	private final BiomeSelectorLayer start;
	private final Biome fallback;

	/**
	 * Constructs a new layered biome selector.
	 *
	 * @param start The starting layer.
	 * @param fallback The fallback biome in case of a null biome.
	 */
	public LayeredBiomeSelector(BiomeSelectorLayer start, Biome fallback) {
		this.start = start;
		this.fallback = fallback;
	}

	@Override
	public Biome pickBiome(int x, int y, int z, long seed) {
		LayeredBiomeSelectorElement current = start;
		while (!(current instanceof Biome)) {
			final LayeredBiomeSelectorElement next =
					((BiomeSelectorLayer) current).pick(x, y, z, seed);
			if (next == null) {
				Spout.getLogger().log(Level.WARNING, "Got a null element in biome selector."
						+ " Check your ranges in layer: " + current + "."
						+ " Using " + fallback.getName() + " as a fallback biome for now.");
				return fallback;
			}
			current = next;
		}
		return (Biome) current;
	}
}
