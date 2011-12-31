package org.getspout.server.util.thread.snapshotable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable class for HashSets
 */
public class SnapshotableHashSet<T> implements Snapshotable {
	private ConcurrentLinkedQueue<SnapshotUpdate<T>> pendingUpdates = new ConcurrentLinkedQueue<SnapshotUpdate<T>>();
	
	private HashSet<T> snapshot;
	
	public SnapshotableHashSet(SnapshotManager manager, HashSet<T> initial) {
		snapshot = new HashSet<T>();
		for (T o : initial) {
			add(o);
		}
		manager.add(this);
	}
	
	/**
	 * Adds an object to the list
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void add(T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, true));
	}
	
	
	/**
	 * Removes an object from the list
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void remove(T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, false));
	}
	
	/**
	 * Gets the snapshot value 
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public Set<T> get() {
		return Collections.unmodifiableSet(snapshot);
	}
	
	/**
	 * Copies the next values to the snapshot
	 */
	public void copySnapshot() {
		SnapshotUpdate<T> update;
		while ((update = pendingUpdates.poll()) != null) {
			processUpdate(update);
		}
	}
	
	private void processUpdate(SnapshotUpdate<T> update) {
		if (update.isIndexed()) {
			throw new IllegalStateException("Hash sets do not support indexed operation");
		} else {
			if (update.isAdd()) {
				snapshot.add(update.getObject());
			} else {
				snapshot.remove(update.getObject());
			}
		}
	}

}
