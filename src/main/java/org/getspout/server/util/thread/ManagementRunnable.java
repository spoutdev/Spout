package org.getspout.server.util.thread;

import java.io.Serializable;

import org.getspout.server.util.thread.future.ManagedFuture;

/**
 * This task must support being serialized and then the deserialized object being run instead
 * 
 * This task does not have a return a value
 */
public abstract class ManagementRunnable extends ManagementTask {

	private static final long serialVersionUID = 1L;

	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final ManagedFuture<Serializable> getFuture() {
		return null;
	}
	
	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final void setFuture(ManagedFuture<Serializable> future) {
		if (future != null) {
			future.set(null);
		}
	}
	
}
