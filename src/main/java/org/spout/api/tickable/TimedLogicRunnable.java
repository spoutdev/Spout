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
package org.spout.api.tickable;

/**
 * Represents a timed {@link LogicRunnable}.
 * This process easily allows looping of a continuous task or one timed task on the {@link BasicTickable}.
 * @param <T>
 */
public abstract class TimedLogicRunnable<T extends Tickable> extends LogicRunnable<T> {
	protected float delay, maxDelay;
	private boolean loop;

	/**
	 * Constructs a new TimedLogicRunnable. Initializes the process to be registered to a {@link BasicTickable}.
	 * @param parent the tickable the process belongs to
	 * @param delay the delay, in seconds, until {@link TimedLogicRunnable#run()} is called.
	 * @param loop whether or not the process should start over again once completed.
	 */
	public TimedLogicRunnable(T parent, float delay, boolean loop) {
		super(parent);
		this.delay = maxDelay = delay;
		this.loop = loop;
	}

	/**
	 * Constructs a new TimedLogicRunnable. Initializes the process to be registered to a {@link BasicTickable}.
	 * @param parent the tickable the process belongs to
	 * @param delay the delay, in seconds, until {@link TimedLogicRunnable#run()} is called.
	 */
	public TimedLogicRunnable(T parent, float delay) {
		this(parent, delay, false);
	}

	/**
	 * Whether or not the process should loop.
	 * @return true if process loops.
	 */
	public boolean loops() {
		return loop;
	}

	/**
	 * Sets whether or not the process should loop.
	 * @param loop whether the process should loop
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Gets the delay, in seconds, until the process is executed.
	 * @return delay
	 */
	public float getDelay() {
		return delay;
	}

	/**
	 * Sets the delay, in seconds, until the process is executed.
	 * @param delay
	 */
	public void setDelay(float delay) {
		this.delay = delay;
	}

	/**
	 * Gets the maximum delay, in seconds, that the delay will be set to when it expires.
	 * @return maximumDelay
	 */
	public float getMaxDelay() {
		return maxDelay;
	}

	/**
	 * Sets the maximum delay, in seconds, that the delay will be set to when it expires.
	 * @param maxDelay
	 */
	public void setMaxDelay(float maxDelay) {
		this.maxDelay = maxDelay;
	}

	@Override
	public boolean shouldRun(float dt) {
		delay -= dt;
		if (delay <= 0) {
			delay = maxDelay;
			return true;
		}
		return false;
	}
}
