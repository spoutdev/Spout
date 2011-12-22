package org.getspout.server.util.thread;

import java.io.Serializable;

import org.getspout.api.util.future.SimpleFuture;

/**
 * This task must support being serialized and then the deserialized object being run instead
 * 
 * This task does not have a return a value
 */
public abstract class ManagementRunnable implements Serializable, ManagementTask {

	private static final long serialVersionUID = 1L;

	/**
	 * A Runnable doesn't return a value, which is the same as a snapshot read
	 */
	public final boolean isSnapshotRead() {
		return true;
	}
	
	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final SimpleFuture<Serializable> getFuture() {
		return null;
	}
	
	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final void setFuture(SimpleFuture<Serializable> future) {
		if (future != null) {
			future.set(null);
		}
	}
	
}
