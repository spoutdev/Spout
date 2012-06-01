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
package org.spout.engine.util.thread.snapshotable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object for ArrayLists
 */
public class SnapshotableArrayList<T> implements Snapshotable {
	private ConcurrentLinkedQueue<SnapshotUpdate<T>> pendingUpdates = new ConcurrentLinkedQueue<SnapshotUpdate<T>>();
	private ArrayList<T> snapshot;

	public SnapshotableArrayList(SnapshotManager manager, ArrayList<T> initial) {
		snapshot = new ArrayList<T>();
		if (initial != null) {
			for (T o : initial) {
				add(o);
			}
		}
		manager.add(this);
	}

	/**
	 * Adds an object to the list
	 * @param next
	 */
	@DelayedWrite
	public void add(T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, true));
	}

	/**
	 * Removes an object from the list
	 * @param next
	 */
	@DelayedWrite
	public void remove(T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, false));
	}

	/**
	 * Adds an object to the list at a particular index
	 * @param next
	 */
	@DelayedWrite
	public void add(int index, T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, index, true));
	}

	/**
	 * Removes the object from the list at a particular index
	 * @param next
	 */
	@DelayedWrite
	public void remove(int index) {
		pendingUpdates.add(new SnapshotUpdate<T>(index, false));
	}

	/**
	 * Gets the snapshot value
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public List<T> get() {
		return Collections.unmodifiableList(snapshot);
	}

	/**
	 * Copies the next values to the snapshot
	 */
	@Override
	public void copySnapshot() {
		SnapshotUpdate<T> update;
		while ((update = pendingUpdates.poll()) != null) {
			processUpdate(update);
		}
	}

	private void processUpdate(SnapshotUpdate<T> update) {
		if (update.isIndexed()) {
			if (update.isAdd()) {
				snapshot.add(update.getIndex(), update.getObject());
			} else {
				snapshot.remove(update.getIndex());
			}
		} else {
			if (update.isAdd()) {
				snapshot.add(update.getObject());
			} else {
				snapshot.remove(update.getObject());
			}
		}
	}
}
