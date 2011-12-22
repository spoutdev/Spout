package org.getspout.server.util.thread;

public abstract class Managed {
	private final AsyncManager manager;

	public Managed(AsyncManager manager) {
		manager.addManaged(this);
		this.manager = manager;
	}

	/**
	 * Returns the thread that is managing this object
	 *
	 * @return the management thread
	 */
	public AsyncManager getManager() {
		return manager;
	}
}
