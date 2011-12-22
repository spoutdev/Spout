package org.getspout.server.util.thread.future;

import org.getspout.api.util.future.SimpleFuture;
import org.getspout.server.util.thread.AsyncManager;

/**
 * This is a future that is linked to a particular AsyncManager
 */
public class ManagedFuture<T> extends SimpleFuture<T> {
	
	private AsyncManager manager;
	
	public ManagedFuture() {
		super();
	}
	
	public ManagedFuture(T result) {
		super(result);
	}
	
	public ManagedFuture(AsyncManager manager, T result) {
		super(result);
		this.manager = manager;
	}
	
	/**
	 * Gets manager associated with this future
	 * 
	 * @return the manager
	 */
	public AsyncManager getManager() {
		return manager;
	}
	
	/**
	 * Sets the manager associated with this future
	 * 
	 * @return the manager
	 */
	public void setManager(AsyncManager manager) {
		this.manager = manager;
	}
	
}
