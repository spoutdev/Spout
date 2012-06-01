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

public class SnapshotUpdate<T> {
	private final boolean add;
	private final int index;
	private final T object;

	public SnapshotUpdate(T object, boolean add) {
		this.object = object;
		this.add = add;
		this.index = -1;
	}

	public SnapshotUpdate(int index, boolean add) {
		if (index < 0) {
			throw new IllegalArgumentException("Negative indexs are not supported");
		} else if (add) {
			throw new IllegalArgumentException("An object must be provided when adding an object");
		}
		this.object = null;
		this.add = add;
		this.index = index;
	}

	public SnapshotUpdate(T object, int index, boolean add) {
		if (index < 0) {
			throw new IllegalArgumentException("Negative indexs are not supported");
		} else if (!add) {
			throw new IllegalStateException("Removal of objects does not require both an index and an object");
		}
		this.object = object;
		this.add = add;
		this.index = index;
	}

	/**
	 * Indicates if this update is an addition or removal
	 * @return true for additions
	 */
	public boolean isAdd() {
		return add;
	}

	/**
	 * Indicates if this is an indexed operation
	 * @return true for indexed operations
	 */
	public boolean isIndexed() {
		return index >= 0;
	}

	/**
	 * Gets the object
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Gets the index
	 * @return
	 */
	public int getIndex() {
		if (!isIndexed()) {
			throw new IllegalStateException("Cannot get the index of a non-indexed operation");
		}
		return index;
	}
}
