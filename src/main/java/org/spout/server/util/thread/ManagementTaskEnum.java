/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.util.thread;

import java.util.HashSet;
import java.util.concurrent.Callable;

import org.spout.server.util.thread.coretasks.CopySnapshotTask;
import org.spout.server.util.thread.coretasks.PreSnapshotTask;
import org.spout.server.util.thread.coretasks.StartTickTask;

public enum ManagementTaskEnum {

	COPY_SNAPSHOT(1, new Callable<CopySnapshotTask>() {
		@Override
		public CopySnapshotTask call() {
			return new CopySnapshotTask();
		}
	}),
	START_TICK(2, new Callable<StartTickTask>() {
		@Override
		public StartTickTask call() {
			return new StartTickTask();
		}
	}),
	PRE_SNAPSHOT(3, new Callable<PreSnapshotTask>() {
		@Override
		public PreSnapshotTask call() {
			return new PreSnapshotTask();
		}
	});

	private static final int maxId = 3;
	private static final HashSet<Integer> ids = new HashSet<Integer>();

	static {
		for (ManagementTaskEnum e : ManagementTaskEnum.values()) {
			reserveId(e.getId());
		}
	}

	private int id;
	private Callable<? extends ManagementTask> create;

	private ManagementTaskEnum(int id, Callable<? extends ManagementTask> create) {
		this.id = id;
		this.create = create;
	}

	public int getId() {
		return id;
	}

	public ManagementTask getInstance() {
		try {
			return create.call();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create class for management task", e);
		}
	}

	private static void reserveId(int id) {
		if (id > maxId) {
			throw new IllegalArgumentException("Task id exceeds the maximum id value, please update the ManagementTask class");
		}
		if (!ids.add(id)) {
			throw new IllegalArgumentException("The task id of " + id + " was registered more than once");
		}
	}

	/**
	 * Gets the highest registered id
	 *
	 * @return the highest id
	 */
	public static int getMaxId() {
		return maxId;
	}

}
