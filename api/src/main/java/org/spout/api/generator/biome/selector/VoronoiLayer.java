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
package org.spout.api.generator.biome.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.royawesome.jlibnoise.module.modifier.Turbulence;
import net.royawesome.jlibnoise.module.source.Voronoi;

import org.spout.math.GenericMath;

/**
 * A layer where each voronoi cell is a {@link LayeredBiomeSelectorElement}. This layer uses a voronoi source passed through a turbulence modifier. Useful for land with various unorganized biomes.
 */
public class VoronoiLayer implements BiomeSelectorLayer, Cloneable {
	private final List<LayeredBiomeSelectorElement> selectorElements = new ArrayList<>();
	private final Voronoi voronoi = new Voronoi();
	private final Turbulence turbulence = new Turbulence();
	private final int uniquenessValue;

	/**
	 * Construct a new voronoi layer.
	 *
	 * @param uniquenessValue Layers with the same uniqueness value will pick the same elements for the same seed. Different values will often result in different elements. The value should be a prime
	 * number.
	 */
	public VoronoiLayer(int uniquenessValue) {
		this.uniquenessValue = uniquenessValue;
		voronoi.setDisplacement(1);
		turbulence.SetSourceModule(0, voronoi);
	}

	@Override
	public LayeredBiomeSelectorElement pick(int x, int y, int z, long seed) {
		voronoi.setSeed((int) seed * uniquenessValue);
		turbulence.setSeed((int) seed * uniquenessValue * uniquenessValue);
		final float size = selectorElements.size() / 2f;
		return selectorElements.get(GenericMath.floor(turbulence.GetValue(x, y, z) * size + size));
	}

	/**
	 * Adds multiple ranged elements to the layer.
	 *
	 * @param elements The element to be added.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer addElements(LayeredBiomeSelectorElement... elements) {
		return addElements(Arrays.asList(elements));
	}

	/**
	 * Adds a collection of ranged elements to the layer.
	 *
	 * @param elements The element to be added.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer addElements(Collection<LayeredBiomeSelectorElement> elements) {
		this.selectorElements.addAll(elements);
		return this;
	}

	/**
	 * Sets the voronoi frequency.
	 *
	 * @param frequency The frequency.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer setVoronoiFrequency(double frequency) {
		voronoi.setFrequency(frequency);
		return this;
	}

	/**
	 * Sets the turbulence frequency.
	 *
	 * @param frequency The frequency.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer setTurbulenceFrequency(double frequency) {
		turbulence.setFrequency(frequency);
		return this;
	}

	/**
	 * Sets the turbulence power.
	 *
	 * @param power The power.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer setTurbulencePower(double power) {
		turbulence.setPower(power);
		return this;
	}

	/**
	 * Sets the turbulence roughness.
	 *
	 * @param roughness The roughness.
	 * @return The layer itself for chained calls.
	 */
	public VoronoiLayer setTurbulenceRoughness(int roughness) {
		turbulence.setRoughness(roughness);
		return this;
	}

	@Override
	public VoronoiLayer clone() {
		return new VoronoiLayer(uniquenessValue).
				setVoronoiFrequency(voronoi.getFrequency()).
				setTurbulenceFrequency(turbulence.getFrequency()).
				setTurbulencePower(turbulence.getPower()).
				setTurbulenceRoughness(turbulence.getRoughnessCount()).
				addElements(selectorElements);
	}
}
