package org.getspout.server.util.thread.snapshotable;

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
	 *
	 * @return true for additions
	 */
	public boolean isAdd() {
		return add;
	}

	/**
	 * Indicates if this is an indexed operation
	 *
	 * @return true for indexed operations
	 */
	public boolean isIndexed() {
		return index >= 0;
	}

	/**
	 * Gets the object
	 *
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Gets the index
	 *
	 * @return
	 */
	public int getIndex() {
		if (!isIndexed()) {
			throw new IllegalStateException("Cannot get the index of a non-indexed operation");
		}
		return index;
	}

}
