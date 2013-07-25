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

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.module.modifier.Clamp;
import net.royawesome.jlibnoise.module.source.Perlin;

/**
 * A layer split into several ranges of values calculated using a
 * {@link net.royawesome.jlibnoise.module.source.Perlin}. Useful for land. This
 * layer has a value range of 1 to -1 (inclusive).
 */
public class PerlinRangeLayer extends NoiseRangeLayer implements Cloneable {
	private final Perlin perlin = new Perlin();
	private final Clamp clamp = new Clamp();
	private final int uniquenessValue;

	/**
	 * Construct a new perlin layer.
	 *
	 * @param uniquenessValue Layers with the same uniqueness value will pick
	 * the same elements for the same seed. Different values will often result
	 * in different elements. The value should be a prime number.
	 */
	public PerlinRangeLayer(int uniquenessValue) {
		this.uniquenessValue = uniquenessValue;
		clamp.SetSourceModule(0, perlin);
		clamp.setLowerBound(-1);
		clamp.setUpperBound(1);
	}

	@Override
	protected float getNoiseValue(int x, int y, int z, int seed) {
		perlin.setSeed(seed * uniquenessValue);
		return (float) clamp.GetValue(x, y, z);
	}

	/**
	 * Sets the perlin frequency.
	 *
	 * @param frequency The frequency.
	 * @return The layer itself for chained calls.
	 */
	public PerlinRangeLayer setFrequency(double frequency) {
		perlin.setFrequency(frequency);
		return this;
	}

	/**
	 * Sets the perlin lacunarity.
	 *
	 * @param lacunarity The lacunarity.
	 * @return The layer itself for chained calls.
	 */
	public PerlinRangeLayer setLacunarity(double lacunarity) {
		perlin.setLacunarity(lacunarity);
		return this;
	}

	/**
	 * Sets the perlin quality.
	 *
	 * @param quality The quality.
	 * @return The layer itself for chained calls.
	 */
	public PerlinRangeLayer setNoiseQuality(NoiseQuality quality) {
		perlin.setNoiseQuality(quality);
		return this;
	}

	/**
	 * Sets the perlin octave count.
	 *
	 * @param octaveCount The octave count.
	 * @return The layer itself for chained calls.
	 */
	public PerlinRangeLayer setOctaveCount(int octaveCount) {
		perlin.setOctaveCount(octaveCount);
		return this;
	}

	/**
	 * Sets the perlin persistence.
	 *
	 * @param persistence The persistence.
	 * @return The layer itself for chained calls.
	 */
	public PerlinRangeLayer setPersistence(double persistence) {
		perlin.setPersistence(persistence);
		return this;
	}

	@Override
	public PerlinRangeLayer clone() {
		return (PerlinRangeLayer) new PerlinRangeLayer(uniquenessValue).
				setFrequency(perlin.getFrequency()).
				setLacunarity(perlin.getLacunarity()).
				setNoiseQuality(perlin.getNoiseQuality()).
				setOctaveCount(perlin.getOctaveCount()).
				setPersistence(perlin.getPersistence()).
				addElements(ranges);
	}
}
