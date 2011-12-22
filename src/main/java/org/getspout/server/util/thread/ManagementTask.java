package org.getspout.server.util.thread;

import java.io.Serializable;

import org.getspout.server.util.thread.future.ManagedFuture;

/**
 * This is a task that returns a value
 * 
 * This task must support being serialized and then the deserialized object being run instead.
 * 
 * Its return value must also support serialization
 */
public abstract class ManagementTask implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected static int taskId = -1;

	public abstract Serializable call(AsyncExecutor executor) throws InterruptedException;
	
	/**
	 * Gets the Future associated with this task
	 * 
	 * @return the Future associated with this task
	 */
	public abstract ManagedFuture<Serializable> getFuture();
	
	/**
	 * Sets the Future associated with this task
	 * 
	 * @param future the future
	 */
	public abstract void setFuture(ManagedFuture<Serializable> future);

}
