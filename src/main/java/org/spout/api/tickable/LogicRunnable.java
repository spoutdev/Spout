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
 * Represents a process for the a main {@link Tickable}
 * @param <T>
 */
public abstract class LogicRunnable<T extends Tickable> implements Runnable, Comparable<LogicRunnable<T>> {
	protected LogicPriority priority;
	protected final T parent;

	/**
	 * Constructs a new process to be registered to a {@link Tickable}.
	 * @param parent the {@link Tickable} the process belongs to.
	 * @param priority the {@link LogicPriority} of the runnable
	 */
	public LogicRunnable(T parent, LogicPriority priority) {
		this.parent = parent;
		this.priority = priority;
	}

	/**
	 * Constructs a new process to be registered to a {@link Tickable} at {@link LogicPriority#NORMAL}.
	 * @param parent the {@link Tickable} the process belongs to.
	 */
	public LogicRunnable(T parent) {
		this(parent, LogicPriority.NORMAL);
	}

	/**
	 * Whether or not the process should run at the given tick.
	 * @param dt
	 * @return true if process should run.
	 */
	public abstract boolean shouldRun(float dt);

	/**
	 * Registers the process to it's assigned {@link Tickable}
	 */
	public void register() {
		parent.registerProcess(this);
	}

	/**
	 * Unregisters the process to it's assigned {@link Tickable}
	 */
	public void unregister() {
		parent.unregisterProcess(this);
	}

	/**
	 * Called when the process is registered.
	 */
	public void onRegistration() {
	}

	/**
	 * Called when the process is unregistered.
	 */
	public void onUnregistration() {
	}

	/**
	 * Gets the parent of the process.
	 * @return parent the {@link Tickable}
	 */
	public T getParent() {
		return parent;
	}

	/**
	 * Gets the priority of the runnable.
	 * @return priority of runnable
	 */
	public LogicPriority getPriority() {
		return priority;
	}

	/**
	 * Sets the priority of the runnable
	 * @param priority of runnable
	 */
	public void setPriority(LogicPriority priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(LogicRunnable<T> runnable) {
		return runnable.getPriority().getIndex() - priority.getIndex();
	}
}
