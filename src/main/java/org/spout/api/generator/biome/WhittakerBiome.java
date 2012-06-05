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
package org.spout.api.generator.biome;

import org.spout.api.generator.biome.Biome;

/**
 * Defines an abstract biome based on Whittaker's average temperature & average rainfall model. It also incorporates a
 * range of elevation.
 */
public abstract class WhittakerBiome extends Biome {
	private final float rainfall, temperature, minElevation, maxElevation;

	protected WhittakerBiome(float temp, float rain, float min, float max, Decorator... decorators) {
		super(decorators);
		this.rainfall = rain;
		this.temperature = temp;
		this.minElevation = min;
		this.maxElevation = max;
	}

	public final float getTemperature() {
		return this.temperature;
	}

	public final float getRainfall() {
		return this.rainfall;
	}

	public final float getMinElevation() {
		return this.minElevation;
	}

	public final float getMaxElevation() {
		return this.maxElevation;
	}

	/**
	 * Returns the closeness (i.e. distance on the Whittaker Diagram) of the biome's average temperature and rainfall
	 * and the passed temperature and rainfall values.
	 */
	public final double getCloseness(double temp, double rain) {
		return Math.sqrt(Math.pow(rainfall - rain, 2) + Math.pow(temperature - temp, 2));
	}

	/**
	 * Returns true if the passed elevation is within this biome's tolerance.
	 */
	public final boolean isValidElevation(double elevation) {
		return (elevation <= maxElevation && elevation >= minElevation) ? true : false;
	}
}
