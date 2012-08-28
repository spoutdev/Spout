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
package org.spout.api.scheduler;

public class TaskPriority {

	/**
	 * Priority for tasks which may not be deferred
	 */
	public static final TaskPriority CRITICAL = new TaskPriority(0);
	/**
	 * Priority for tasks which can be deferred by up to 50ms when under load
	 */
	public static final TaskPriority HIGHEST = new TaskPriority(50);
	/**
	 * Priority for tasks which can be deferred by up to 150ms when under load
	 */
	public static final TaskPriority HIGH = new TaskPriority(150);
	/**
	 * Priority for tasks which can be deferred by up to 500ms when under load
	 */
	public static final TaskPriority MEDIUM = new TaskPriority(500);
	/**
	 * Priority for tasks which can be deferred by up to 500ms when under load
	 */
	public static final TaskPriority NORMAL = MEDIUM;
	/**
	 * Priority for tasks which can be deferred by up to 1.5s when under load
	 */
	public static final TaskPriority LOW = new TaskPriority(1500);
	/**
	 * Priority for tasks which can be deferred by up to 10s when under load
	 */
	public static final TaskPriority LOWEST = new TaskPriority(10000);

	private final long maxDeferred;
	
	/**
	 * Creates a TaskPriority instance which sets the maximum time that a task can be deferred.
	 * 
	 * @param maxDelay the maximum delay before 
	 */
	public TaskPriority(long maxDeferred) {
		this.maxDeferred = maxDeferred;
	}
	
	/**
	 * Gets the maximum time that the task can be deferred.
	 * 
	 * @return
	 */
	public long getMaxDeferred() {
		return maxDeferred;
	}
	
}
