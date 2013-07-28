/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.util.thread.snapshotable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * A snapshotable object for ArrayLists
 */
public class SnapshotableArrayList<T> implements Snapshotable {
	private final ConcurrentLinkedQueue<T> dirty = new ConcurrentLinkedQueue<>();
	private final List<T> snapshot;
	private final List<T> live;

	public SnapshotableArrayList(SnapshotManager manager) {
		this(manager, null);
	}

	public SnapshotableArrayList(SnapshotManager manager, ArrayList<T> initial) {
		if (initial != null) {
			snapshot = new ArrayList<>(initial);
		} else {
			snapshot = new ArrayList<>();
		}
		live = Collections.synchronizedList(new ArrayList<>(snapshot));
		manager.add(this);
	}

	/**
	 * Adds an object to the list
	 */
	@DelayedWrite
	public boolean add(T object) {
		boolean success = live.add(object);

		if (success) {
			dirty.add(object);
		}

		return success;
	}

	@DelayedWrite
	public void addAll(Collection<T> values) {
		for (T object : values) {
			boolean success = live.add(object);

			if (success) {
				dirty.add(object);
			}
		}
	}

	/**
	 * Removes an object from the list
	 */
	@DelayedWrite
	public boolean remove(T object) {
		boolean success = live.remove(object);

		if (success) {
			dirty.add(object);
		}

		return success;
	}

	/**
	 * Removes the object from the list at a particular index
	 */
	@DelayedWrite
	public void remove(int index) {
		dirty.add(live.remove(index));
	}

	/**
	 * Gets the snapshot value
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public List<T> get() {
		return Collections.unmodifiableList(snapshot);
	}

	/**
	 * Gets the live value
	 *
	 * @return the live value
	 */
	@LiveRead
	public List<T> getLive() {
		return Collections.unmodifiableList(live);
	}

	/**
	 * Gets the dirty object list
	 *
	 * @return the dirty list
	 */
	@LiveRead
	public List<T> getDirtyList() {
		return Collections.unmodifiableList(new ArrayList<>(dirty));
	}

	/**
	 * Copies the next values to the snapshot
	 */
	@Override
	public void copySnapshot() {
		if (dirty.size() > 0) {
			snapshot.clear();
			synchronized (live) {
				for (T o : live) {
					snapshot.add(o);
				}
			}
		}
		dirty.clear();
	}
}
