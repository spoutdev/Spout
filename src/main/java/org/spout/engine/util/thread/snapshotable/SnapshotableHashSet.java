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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.scheduler.TickStage;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable class for HashSets
 */
public class SnapshotableHashSet<T> implements Snapshotable {
	private final Set<T> snapshot = new HashSet<T>();
	private final Set<T> unmodifySnapshot = Collections.unmodifiableSet(snapshot);
	private final Set<T> live = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
	private final Set<T> unmodifyLive = Collections.unmodifiableSet(live);
	private final ConcurrentLinkedQueue<T> dirty = new ConcurrentLinkedQueue<T>();
	private final ArrayList<T> dirtyList = new ArrayList<T>();
	private final HashSet<T> dirtyListTemp = new HashSet<T>();
	private final List<T> unmodifyDirty = Collections.unmodifiableList(dirtyList);
	private boolean dirtyListGenerated = false;

	public SnapshotableHashSet(SnapshotManager manager) {
		this(manager, null);
	}

	public SnapshotableHashSet(SnapshotManager manager, HashSet<T> initial) {
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
	 * @return true if the object was successfully added
	 */
	@DelayedWrite
	@LiveRead
	public boolean add(T object) {
		boolean success = live.add(object);
		if (success) {
			dirty.add(object);
		}
		return success;
	}

	/**
	 * Removes an object from the list
	 * @param next
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
	 * Gets the snapshot value
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public Set<T> get() {
		return unmodifySnapshot;
	}

	/**
	 * Gets the live value
	 * @return the live set
	 */
	public Set<T> getLive() {
		return unmodifyLive;
	}

	/**
	 * Creates a list of elements that have been changed since the last snapshot
	 * copy.<br>
	 * <br>
	 * This method may only be called during the pre-snapshot stage and the list
	 * only remains valid during that stage.
	 * @return the list of elements that have been updated
	 */
	public List<T> getDirtyList() {
		TickStage.checkStage(TickStage.PRESNAPSHOT);
		if (!dirtyListGenerated) {
			for (T o : dirty) {
				if (dirtyListTemp.add(o)) {
					dirtyList.add(o);
				}
			}
			dirtyListTemp.clear();
			dirtyListGenerated = true;
		}
		return unmodifyDirty;
	}

	/**
	 * Tests if the set is empty
	 * @return true if the set is empty
	 */
	public boolean isEmptyLive() {
		return live.isEmpty();
	}

	/**
	 * Copies the next values to the snapshot
	 */
	@Override
	public void copySnapshot() {
		for (T o : dirty) {
			if (live.contains(o)) {
				snapshot.add(o);
			} else {
				snapshot.remove(o);
			}
		}
		dirty.clear();
		dirtyList.clear();
		dirtyListGenerated = false;
	}
}
