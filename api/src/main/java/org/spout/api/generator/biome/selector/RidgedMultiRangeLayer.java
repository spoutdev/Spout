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
import net.royawesome.jlibnoise.module.source.RidgedMulti;

/**
 * A layer split into several ranges of values calculated using a
 * {@link net.royawesome.jlibnoise.module.source.RidgedMulti}. Useful for
 * mountains and rivers. This layer has a value range of 1 to -1 (inclusive).
 */
public class RidgedMultiRangeLayer extends NoiseRangeLayer implements Cloneable {
	private final RidgedMulti ridgedMulti = new RidgedMulti();
	private final Clamp clamp = new Clamp();
	private final int uniquenessValue;

	/**
	 * Construct a new ridged multi layer.
	 *
	 * @param uniquenessValue Layers with the same uniqueness value will pick
	 * the same elements for the same seed. Different values will often result
	 * in different elements. The value should be a prime number.
	 */
	public RidgedMultiRangeLayer(int uniquenessValue) {
		this.uniquenessValue = uniquenessValue;
		clamp.SetSourceModule(0, ridgedMulti);
		clamp.setLowerBound(-1);
		clamp.setUpperBound(1);
	}

	@Override
	protected float getNoiseValue(int x, int y, int z, int seed) {
		ridgedMulti.setSeed(seed * uniquenessValue);
		return (float) clamp.GetValue(x, y, z);
	}

	/**
	 * Sets the ridged multi frequency.
	 *
	 * @param frequency The frequency.
	 * @return The layer itself for chained calls.
	 */
	public RidgedMultiRangeLayer setFrequency(double frequency) {
		ridgedMulti.setFrequency(frequency);
		return this;
	}

	/**
	 * Sets the ridged multi lacunarity.
	 *
	 * @param lacunarity The lacunarity.
	 * @return The layer itself for chained calls.
	 */
	public RidgedMultiRangeLayer setLacunarity(double lacunarity) {
		ridgedMulti.setLacunarity(lacunarity);
		return this;
	}

	/**
	 * Sets the ridged multi quality.
	 *
	 * @param quality The quality.
	 * @return The layer itself for chained calls.
	 */
	public RidgedMultiRangeLayer setNoiseQuality(NoiseQuality quality) {
		ridgedMulti.setNoiseQuality(quality);
		return this;
	}

	/**
	 * Sets the ridged multi octave count.
	 *
	 * @param octaveCount The octave count.
	 * @return The layer itself for chained calls.
	 */
	public RidgedMultiRangeLayer setOctaveCount(int octaveCount) {
		ridgedMulti.setOctaveCount(octaveCount);
		return this;
	}

	@Override
	public RidgedMultiRangeLayer clone() {
		return (RidgedMultiRangeLayer) new RidgedMultiRangeLayer(uniquenessValue).
				setFrequency(ridgedMulti.getFrequency()).
				setLacunarity(ridgedMulti.getLacunarity()).
				setNoiseQuality(ridgedMulti.getNoiseQuality()).
				setOctaveCount(ridgedMulti.getOctaveCount()).
				addElements(ranges);
	}
}
