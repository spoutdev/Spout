package org.getspout.server.util.thread.snapshotable;

public interface Snapshotable {
	
	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot();

}
