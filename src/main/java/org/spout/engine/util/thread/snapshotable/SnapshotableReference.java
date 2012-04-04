/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports basic class types.
 *
 * This class should be used for immutable types that are updated by replacing
 * with a new immutable object
 *
 * @param <T> the underlying type
 */
public class SnapshotableReference<T> implements Snapshotable {
	private AtomicReference<T> next = new AtomicReference<T>();
	private T snapshot;

	public SnapshotableReference(SnapshotManager manager, T initial) {
		next.set(initial);
		snapshot = initial;
		manager.add(this);
	}

	/**
	 * Sets the next value for the Snapshotable
	 *
	 * @param next
	 */
	@DelayedWrite
	public void set(T next) {
		this.next.set(next);
	}

	/**
	 * Sets the live value to update, if the live value is equal to expect.
	 *
	 * @param expect the expected value
	 * @param update the new value
	 * @return true on success
	 */
	@DelayedWrite
	public boolean compareAndSet(T expect, T update) {
		return next.compareAndSet(expect, update);
	}

	/**
	 * Gets the snapshot value for
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public T get() {
		return snapshot;
	}

	/**
	 * Gets the live value
	 *
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public T getLive() {
		return next.get();
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next.get();
	}

}
