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
package org.spout.api.util.list.concurrent;

import java.util.ArrayList;
import java.util.Collection;

public class ConcurrentList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;
	/**
	 * A list of elements pending to be added
	 */
	private final ArrayList<T> toAdd = new ArrayList<>();
	/**
	 * A list of elements pending to be removed
	 */
	private final ArrayList<T> toRemove = new ArrayList<>();

	/**
	 * Adds all current values to the removal queue
	 */
	public void clearDelayed() {
		synchronized (toAdd) {
			toAdd.clear();
		}
		synchronized (toRemove) {
			toRemove.clear();
			toRemove.addAll(this);
		}
	}

	/**
	 * Adds a value to the addition queue for this list
	 *
	 * @param value to add
	 */
	public void addDelayed(T value) {
		synchronized (toAdd) {
			toAdd.add(value);
		}
	}

	/**
	 * Adds all values to the addition queue for this list
	 *
	 * @param values to add
	 */
	public void addAllDelayed(Collection<? extends T> values) {
		synchronized (toAdd) {
			toAdd.addAll(values);
		}
	}

	/**
	 * Adds a value to the removal queue for this list
	 *
	 * @param value to remove
	 */
	public void removeDelayed(T value) {
		synchronized (toRemove) {
			toRemove.add(value);
		}
	}

	/**
	 * Adds all values to the removal queue for this list
	 *
	 * @param values to remove
	 */
	public void removeAllDelayed(Collection<? extends T> values) {
		synchronized (toRemove) {
			toRemove.addAll(values);
		}
	}

	/**
	 * Synchronizes this list with all previously done delayed operations
	 */
	public void sync() {
		synchronized (toAdd) {
			super.addAll(toAdd);
			toAdd.clear();
		}
		synchronized (toRemove) {
			super.removeAll(toRemove);
			toRemove.clear();
		}
	}
}
