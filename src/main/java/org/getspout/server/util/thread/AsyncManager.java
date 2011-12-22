package org.getspout.server.util.thread;

import java.util.WeakHashMap;

public abstract class AsyncManager {
	
	private final AsyncExecutor executor;
	private final WeakHashMap<Managed,Boolean> managedSet = new WeakHashMap<Managed,Boolean>();
	
	public AsyncManager(AsyncExecutor executor) {
		this.executor = executor;
		executor.setManager(this);
	}

	/**
	 * Sets this AsyncManager as manager for a given object
	 *
	 * @param managed the object to give responsibility for
	 */
	public final void addManaged(Managed managed) {
		managedSet.put(managed, Boolean.TRUE);
	}
	
	/**
	 * Gets the associated AsyncExecutor
	 * 
	 * @return the executor
	 */
	public final AsyncExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * This method is called in order to update the snapshot at the end of each tick
	 */
	public abstract void copySnapshotRun() throws InterruptedException;

	/**
	 * This method is called in order to start a new tick
	 * 
	 * @param tick this number increases by one every tick
	 */
	public abstract void startTickRun(long tick) throws InterruptedException;
}
