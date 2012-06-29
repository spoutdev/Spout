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
package org.spout.api.util;

import org.spout.api.math.MathHelper;

/**
 * Can be used to measure the execution time of a block of code
 */
public class NanoStopWatch {
	private long lastTime, totalTime, startTime;

	/**
	 * Starts a new measurement
	 * @return this stop watch
	 */
	public NanoStopWatch start() {
		this.startTime = System.nanoTime();
		return this;
	}

	/**
	 * Adds the current time to the total and starts a new measurement
	 * @return this stop watch
	 */
	public NanoStopWatch stop() {
		this.lastTime = System.nanoTime() - this.startTime;
		this.totalTime += this.lastTime;
		return this.start();
	}

	/**
	 * Resets this stop watch, but does not stop the measurement<br>
	 * This will set the total time to 0.
	 * @return this stop watch
	 */
	public NanoStopWatch reset() {
		this.lastTime = this.totalTime = 0;
		return this;
	}

	/**
	 * Sets the total time in nanoseconds for this stop watch
	 * @param totalTime to set to
	 * @return this stop watch
	 */
	public NanoStopWatch setTimeNano(long totalTime) {
		this.totalTime = totalTime;
		return this;
	}

	/**
	 * Gets the total time in nanoseconds of the last measurement
	 * @return total time in nanoseconds
	 */
	public long getLastTimeNano() {
		return this.lastTime;
	}

	/**
	 * Gets the total time in nanoseconds
	 * @return total time in nanoseconds
	 */
	public long getTimeNano() {
		return this.totalTime;
	}

	/**
	 * Gets the total time in milliseconds
	 * @return total time in milliseconds
	 */
	public double getTimeMillis() {
		return this.totalTime / 1E6;
	}

	/**
	 * Gets the total time in milliseconds rounded down to the amount of decimals specified
	 * @param decimals to round down to
	 * @return total rounded time in milliseconds
	 */
	public double getRoundedTimeMillis(int decimals) {
		return MathHelper.round(this.getTimeMillis(), decimals);
	}

	/**
	 * Gets the total time in seconds
	 * @return total time in seconds
	 */
	public double getTimeSeconds() {
		return this.totalTime / 1E9;
	}

	/**
	 * Gets the total time as a percentage of another time
	 * @param totalNanoTime to compare with
	 * @return the percentage
	 */
	public double getTimePercentage(NanoStopWatch totalTime) {
		return getTimePercentage(totalTime.getTimeNano());
	}

	/**
	 * Gets the total time as a percentage of another time
	 * @param totalTime Nano Stopwatch to compare with
	 * @return the percentage
	 */
	public double getTimePercentage(long totalNanoTime) {
		return (double) this.totalTime / (double) totalNanoTime * 1E3;
	}

	@Override
	public String toString() {
		return this.getRoundedTimeMillis(3) + " ms";
	}
}
