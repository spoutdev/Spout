package org.getspout.server.util.thread;

import java.io.Serializable;

import org.getspout.api.util.future.SimpleFuture;

/**
 * This is a task that returns a value
 * 
 * This task must support being serialized and then the deserialized object being run instead.
 * 
 * Its return value must also support serialization
 */
public interface ManagementTask {
	
	public Serializable call(ManagementAsyncExecutor executor) throws InterruptedException;
	
	/**
	 * Indicated that the task is a snapshot read task.  This means that 
	 * 
	 * @return true if this task reads from the stable snapshot
	 */
	public boolean isSnapshotRead();
	
	/**
	 * Gets the Future associated with this task
	 * 
	 * @return the Future associated with this task
	 */
	public SimpleFuture<Serializable> getFuture();
	
	/**
	 * Sets the Future associated with this task
	 * 
	 * @param future the future
	 */
	public void setFuture(SimpleFuture<Serializable> future);

}
