package org.spout.api.scheduler;

public class TaskPriority {

	/**
	 * Priority for tasks which should not be deferred
	 */
	public static final TaskPriority CRITICAL = new TaskPriority(0);
	/**
	 * Priority for tasks which can be deferred by up to 50ms
	 */
	public static final TaskPriority HIGH = new TaskPriority(50);
	/**
	 * Priority for tasks which can be deferred by up to 500ms
	 */
	public static final TaskPriority MEDIUM = new TaskPriority(500);
	/**
	 * Priority for tasks which can be deferred by up to 10s
	 */
	public static final TaskPriority LOW = new TaskPriority(10000);

	private final long maxDeferred;
	
	/**
	 * Creates a TaskPriority instance which sets the maximum time that a task can be deferred.
	 * 
	 * @param maxDelay the maximum delay before 
	 */
	public TaskPriority(long maxDeferred) {
		this.maxDeferred = maxDeferred;
	}
	
	/**
	 * Gets the maximum time that the task can be deferred.
	 * 
	 * @return
	 */
	public long getMaxDeferred() {
		return maxDeferred;
	}
	
}
