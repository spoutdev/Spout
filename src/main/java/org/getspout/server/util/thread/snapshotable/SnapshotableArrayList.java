package org.getspout.server.util.thread.snapshotable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;

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
	 * Adds an object to the list at a particular index
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void add(int index, T object) {
		pendingUpdates.add(new SnapshotUpdate<T>(object, index, true));
	}
	
	
	/**
	 * Removes the object from the list at a particular index
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void remove(int index) {
		pendingUpdates.add(new SnapshotUpdate<T>(index, false));
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
