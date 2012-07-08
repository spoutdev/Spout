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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.api.UnsafeMethod;

public class Tickable implements ITickable {
	/**
	 * A set of active processes to have {@link LogicRunnable#shouldRun(float)} called every tick.
	 */
	protected final List<LogicRunnable<Tickable>> activeProcesses = new ArrayList<LogicRunnable<Tickable>>();

	/**
	 * Called each simulation tick.<br/>
	 * 1       tick  = 1/20 second<br/>
	 * 20      ticks = 1 second<br/>
	 * 1200    ticks = 1 minute<br/>
	 * 72000   ticks = 1 hour<br/>
	 * 1728000 ticks = 1 day
	 * Handles the active processes in the Tickable and calls {@link Tickable#onTick(float)}
	 * 
	 * @param dt time since the last tick in seconds
	 */
	public final void tick(float dt) {
		Collections.sort(activeProcesses);
		for (int i = 0; i < activeProcesses.size(); i++) {
			LogicRunnable<Tickable> process = activeProcesses.get(i);
			if (process != null && process.shouldRun(dt)) {
				process.run();
				if (process instanceof TimedLogicRunnable && !((TimedLogicRunnable<?>) process).loops()) {
					unregisterProcess(process);
				}
			}
		}
		onTick(dt);
	}

	/**
	 * Registers a new process for the Tickable.
	 * Calls {@link LogicRunnable#onRegistration()}
	 * @param process
	 */
	@SuppressWarnings("unchecked")
	public void registerProcess(LogicRunnable<?> process) {
		activeProcesses.add((LogicRunnable<Tickable>) process);
		process.onRegistration();
	}

	/**
	 * Unregisters a process for the Tickable.
	 * Calls {@link LogicRunnable#onUnregistration()}
	 * @param process
	 */
	public void unregisterProcess(LogicRunnable<?> process) {
		activeProcesses.remove(process);
		process.onUnregistration();
	}

	@Override
	@UnsafeMethod
	public void onTick(float dt) {
	}
}
