package org.getspout.server.util.thread.snapshotable;

import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports basic class types.
 *
 * This class should be used for immutable types that are updated by replacing with a new immutable object
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
