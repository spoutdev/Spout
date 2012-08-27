/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util;

/**
 * Simple 2-dimensional Perlin noise generator.
 */
public class Perlin2D {
	public double amplitude;
	public double frequency;
	public int[] primes;

	public Perlin2D(double amplitude, double frequency, int[] primes) {
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.primes = primes;
		if (primes.length < 4) {
			throw new IllegalArgumentException();
		}
	}

	private double findnoise(double x, double y) {
		long n = (int) (Math.floor(x) + Math.floor(y) * primes[0]);
		n = n << 13 ^ n;
		long n2 = n * (n * n * primes[1] + primes[2]) + primes[3] & 0x7fffffff;
		return 1.0 - n2 / 1073741824.0;
	}

	private double interpolate(double a, double b, double x) {
		double f = (1.0 - Math.cos(x * Math.PI)) * 0.5;
		return a * (1.0 - f) + b * f;
	}

	public double noise(double x, double y) {
		x *= frequency;
		y *= frequency;
		double floorx = Math.floor(x);
		double floory = Math.floor(y);
		double s = findnoise(floorx, floory);
		double t = findnoise(floorx + 1, floory);
		double u = findnoise(floorx, floory + 1);
		double v = findnoise(floorx + 1, floory + 1);
		double int1 = interpolate(s, t, x - floorx);
		double int2 = interpolate(u, v, x - floorx);
		return amplitude * 0.5 * interpolate(int1, int2, y - floory);
	}
}
