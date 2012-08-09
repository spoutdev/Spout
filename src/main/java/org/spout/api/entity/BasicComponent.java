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
import org.spout.api.tickable.Tickable;

public class BasicComponent implements Component, Tickable {
	private Entity parent;
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
	public void attachToEntity(Entity parent) {
		this.parent = parent;
	}

	@Override
	public Entity getParent() {
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

	public final void tick(float dt) {
		delay -= dt;
		if (canTick()) {
			onTick(dt);
		}
		if (runOnce) {
			getParent().removeComponent(this.getClass());
		}
	}

	public boolean canTick() {
		return delay > 0 && delay > maxDelay;
	}

	public TickPriority getPriority() {
		return priority;
	}

	public void setPriority(TickPriority priority) {
		this.priority = priority;
	}

	public boolean runOnce() {
		return runOnce;
	}

	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public float getMaxDelay() {
		return maxDelay;
	}

	public void setMaxDelay(float maxDelay) {
		this.maxDelay = maxDelay;
	}
}