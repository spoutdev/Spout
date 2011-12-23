package org.getspout.server.util.thread;

import java.util.WeakHashMap;

public abstract class AsyncManager {
	
	private final AsyncExecutor executor;
	private final WeakHashMap<Managed,Boolean> managedSet = new WeakHashMap<Managed,Boolean>();
	private final ManagementTask[] singletonCache = new ManagementTask[ManagementTaskEnum.getMaxId()];
	
	public AsyncManager(AsyncExecutor executor) {
		this.executor = executor;
		executor.setManager(this);
	}
	
	/**
	 * Gets a singleton task if available.
	 * 
	 * Tasks should be returned to the cache after usage
	 * 
	 * @param taskEnum the enum of the task
	 * @return an instance of task
	 */
	public ManagementTask getSingletonTask(ManagementTaskEnum taskEnum) {
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor)current;
			int taskId = taskEnum.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			ManagementTask task = taskCache[taskId];
			if (task != null) {
				taskCache[taskId] = null;
				return task;
			}
		}
		return taskEnum.getInstance();
	}
	
	/**
	 * Returns a singleton task to the cache
	 * 
	 * Tasks should be returned to the cache after usage
	 * 
	 * @param taskEnum the enum of the task
	 * @return an instance of task
	 */
	public void returnSingletonTask(ManagementTask task) {
		if (!task.getFuture().isDone()) {
			throw new IllegalArgumentException("Tasks with active futures should not be returned to the cache");
		}
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor)current;
			ManagementTaskEnum e = task.getEnum();
			int taskId = e.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			taskCache[taskId] = task;
		}
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
