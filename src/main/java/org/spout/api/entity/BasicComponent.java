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
package org.spout.api.entity;

import org.spout.api.tickable.TickPriority;

public class BasicComponent<T extends Controller> implements Component<T> {
	private T parent;
	private TickPriority priority;
	private boolean runOnce;
	private float delay, maxDelay;

	public BasicComponent() {
		this(TickPriority.NORMAL, false, 0, 0);
	}

	public BasicComponent(TickPriority priority) {
		this(priority, false, 0, 0);
	}

	public BasicComponent(TickPriority priority, boolean runOnce) {
		this(priority, runOnce, 0, 0);
	}

	public BasicComponent(boolean runOnce) {
		this(TickPriority.NORMAL, runOnce, 0, 0);
	}

	public BasicComponent(TickPriority priority, boolean runOnce, float delay, float maxDelay) {
		this.priority = priority;
		this.delay = delay;
		this.maxDelay = maxDelay;
		this.runOnce = runOnce;
	}

	@Override
	public void attachToController(T parent) {
		this.parent = parent;
	}

	@Override
	public T getParent() {
		return parent;
	}

	@Override
	public void onAttached() {
	}

	@Override
	public void onDetached() {
	}

	@Override
	public void onTick(float dt) {

	}

	@Override
	public boolean canTick() {
		return delay > 0 && delay > maxDelay;
	}

	@Override
	public TickPriority getPriority() {
		return priority;
	}

	@Override
	public void setPriority(TickPriority priority) {
		this.priority = priority;
	}

	@Override
	public boolean runOnce() {
		return runOnce;
	}

	@Override
	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}

	@Override
	public float getDelay() {
		return delay;
	}

	@Override
	public void setDelay(float delay) {
		this.delay = delay;
	}

	@Override
	public float getMaxDelay() {
		return maxDelay;
	}

	@Override
	public void setMaxDelay(float maxDelay) {
		this.maxDelay = maxDelay;
	}

	@Override
	public final void tick(float dt) {
		delay -= dt;
		if (canTick()) {
			onTick(dt);
		}
		if (runOnce) {
			getParent().removeComponent(this.getClass());
		}
	}

	@Override
	public int compareTo(Component<T> component) {
		return component.getPriority().getIndex() - this.priority.getIndex();
	}
}